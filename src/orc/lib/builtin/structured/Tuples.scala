//
// Tuples.scala -- Implementations of tuple manipulation sites
// Project OrcScala
//
// $Id: Tuples.scala 2933 2011-12-15 16:26:02Z jthywissen $
//
// Created by dkitchin on March 31, 2011.
//
// Copyright (c) 2011 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//

package orc.lib.builtin.structured

import orc.error.runtime.ArgumentTypeMismatchException
import orc.error.compiletime.typing.TupleSizeException
import orc.error.compiletime.typing.ArgumentTypecheckingException
import orc.error.compiletime.typing.ExpectedType
import orc.values.{ OrcRecord, OrcValue, OrcTuple }
import orc.values.sites._
import orc.types._

object TupleConstructor extends TotalSite with TypedSite {
  override def name = "Tuple"
  def evaluate(args: List[AnyRef]) = OrcTuple(args)

  def orcType() = new SimpleCallableType with StrictType {
    def call(argTypes: List[Type]) = { TupleType(argTypes) }
  }
}

/* 
 * Verifies that a Tuple t has a given number of elements.
 * If the check succeeds, the Some(t) is returned, 
 * else None.
 */
object TupleArityChecker extends PartialSite2 with TypedSite {
  override def name = "TupleArityChecker"
  def eval(x: AnyRef, y: AnyRef) =
    (x, y) match {
      case (OrcTuple(elems), arity: BigInt) =>
        if (elems.size == arity) {
          Some(OrcTuple(elems))
        } else {
          None
        }
      case (_: OrcTuple, a) => throw new ArgumentTypeMismatchException(1, "Integer", if (a != null) a.getClass().toString() else "null")
      case (a, _) => throw new ArgumentTypeMismatchException(0, "Tuple", if (a != null) a.getClass().toString() else "null")
    }

  def orcType() = new BinaryCallableType with StrictType {
    def call(r: Type, s: Type): Type = {
      (r, s) match {
        case (t @ TupleType(elements), IntegerConstantType(i)) => {
          if (elements.size != i) {
            throw new TupleSizeException(i.toInt, elements.size)
          }
          OptionType(t)
        }
        case (t: TupleType, IntegerType) => {
          OptionType(t)
        }
        case (_: TupleType, t) => throw new ArgumentTypecheckingException(1, IntegerType, t)
        case (t, _) => throw new ArgumentTypecheckingException(0, ExpectedType("of some tuple"), t)
      }
    }
  }

}
