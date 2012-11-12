//
// NamedToNameless.scala -- Scala trait NamedToNameless
// Project OrcScala
//
// $Id: NamedToNameless.scala 3099 2012-07-21 02:33:18Z laurenyew $
//
// Created by dkitchin on Jul 10, 2010.
//
// Copyright (c) 2011 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//
package orc.ast.oil.named

import orc.ast.oil.nameless

/** @author dkitchin
  */
// Conversions from named to nameless representations
trait NamedToNameless {

  def namedToNameless(e: Expression, context: List[BoundVar], typecontext: List[BoundTypevar]): nameless.Expression = {
    def toExp(e: Expression): nameless.Expression = namedToNameless(e, context, typecontext)
    def toArg(a: Argument): nameless.Argument = namedToNameless(a, context)
    def toType(t: Type): nameless.Type = namedToNameless(t, typecontext)
    e -> {
      case Stop() => nameless.Stop()
      case a: Argument => namedToNameless(a, context)
      case Call(target, args, typeargs) => nameless.Call(toArg(target), args map toArg, typeargs map { _ map toType })
      case left || right => nameless.Parallel(toExp(left), toExp(right))
      case left > x > right => nameless.Sequence(toExp(left), namedToNameless(right, x :: context, typecontext))
      case left < x < right => nameless.Prune(namedToNameless(left, x :: context, typecontext), toExp(right))
      case left ow right => nameless.Otherwise(toExp(left), toExp(right))
      case DeclareDefs(defs, body) => {
        val defnames = defs map { _.name }
        val opennames = (defs flatMap { _.freevars }).distinct filterNot { defnames contains _ }
        val defcontext = defnames.reverse ::: opennames ::: context
        val bodycontext = defnames.reverse ::: context
        val newdefs = defs map { namedToNameless(_, defcontext, typecontext) }
        val newbody = namedToNameless(body, bodycontext, typecontext)
        val openvars =
          opennames map { x =>
            val i = context indexOf x
            assert(i >= 0)
            i
          }
        nameless.DeclareDefs(openvars, newdefs, newbody)
      }
      case DeclareType(x, t, body) => {
        val newTypeContext = x :: typecontext
        /* A type may be defined recursively, so its name is in scope for its own definition */
        val newt = namedToNameless(t, newTypeContext)
        val newbody = namedToNameless(body, context, newTypeContext)
        nameless.DeclareType(newt, newbody)
      }
      case HasType(body, expectedType) => nameless.HasType(toExp(body), toType(expectedType))
      //transform DeclSecurityLevel to nameless. We just make the body nameless?
      case DeclareSecurityLevel(name,parents,children,body) => toExp(body)

      case HasSecurityLevel(body, level) => toExp(body)
      case Hole(holeContext, holeTypeContext) => {
        val newHoleContext = holeContext mapValues { namedToNameless(_, context) }
        val newHoleTypeContext = holeTypeContext mapValues { namedToNameless(_, typecontext) }
        nameless.Hole(newHoleContext, newHoleTypeContext)
      }
    }
  }

  def namedToNameless(a: Argument, context: List[BoundVar]): nameless.Argument = {
    a -> {
      case Constant(v) => nameless.Constant(v)
      case (x: BoundVar) => {
        var i = context indexOf x
        assert(i >= 0)
        nameless.Variable(i)
      }
      case UnboundVar(s) => nameless.UnboundVariable(s)
    }
  }

  def namedToNameless(t: Type, typecontext: List[BoundTypevar]): nameless.Type = {
    def toType(t: Type): nameless.Type = namedToNameless(t, typecontext)
    t -> {
      case u: BoundTypevar => {
        var i = typecontext indexOf u
        assert(i >= 0)
        nameless.TypeVar(i)
      }
      case Top() => nameless.Top()
      case Bot() => nameless.Bot()
      case FunctionType(typeformals, argtypes, returntype) => {
        val newTypeContext = typeformals ::: typecontext
        val newArgTypes = argtypes map { namedToNameless(_, newTypeContext) }
        val newReturnType = namedToNameless(returntype, newTypeContext)
        nameless.FunctionType(typeformals.size, newArgTypes, newReturnType)
      }
      case TupleType(elements) => nameless.TupleType(elements map toType)
      case RecordType(entries) => {
        val newEntries = entries map { case (s, t) => (s, toType(t)) }
        nameless.RecordType(newEntries)
      }
      case TypeApplication(tycon, typeactuals) => {
        val i = typecontext indexOf tycon
        assert(i >= 0)
        nameless.TypeApplication(i, typeactuals map toType)
      }
      case AssertedType(assertedType) => nameless.AssertedType(toType(assertedType))
      case TypeAbstraction(typeformals, t) => {
        val newTypeContext = typeformals ::: typecontext
        val newt = namedToNameless(t, newTypeContext)
        nameless.TypeAbstraction(typeformals.size, newt)
      }
      case ImportedType(classname) => nameless.ImportedType(classname)
      case ClassType(classname) => nameless.ClassType(classname)
      case VariantType(self, typeformals, variants) => {
        val newTypeContext = self :: typeformals ::: typecontext
        val newVariants =
          for ((name, variant) <- variants) yield {
            (name, variant map { namedToNameless(_, newTypeContext) })
          }
        nameless.VariantType(typeformals.size, newVariants)
      }
      case UnboundTypevar(s) => nameless.UnboundTypeVariable(s)
    }
  }

  def namedToNameless(defn: Def, context: List[BoundVar], typecontext: List[BoundTypevar]): nameless.Def = {
    defn -> {
      case Def(_, formals, body, typeformals, argtypes, returntype) => {
        val newContext = formals.reverse ::: context
        val newTypeContext = typeformals ::: typecontext
        val newbody = namedToNameless(body, newContext, newTypeContext)
        val newArgTypes = argtypes map { _ map { namedToNameless(_, newTypeContext) } }
        val newReturnType = returntype map { namedToNameless(_, newTypeContext) }
        nameless.Def(typeformals.size, formals.size, newbody, newArgTypes, newReturnType)
      }
    }
  }
  
 

}
