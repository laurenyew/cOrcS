//
// TypeVariable.scala -- Scala class TypeVariable
// Project OrcScala
//
// $Id: TypeVariable.scala 2933 2011-12-15 16:26:02Z jthywissen $
//
// Created by dkitchin on Nov 26, 2010.
//
// Copyright (c) 2011 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//
package orc.types

/**
  *
  * @author dkitchin
  */
class TypeVariable(val optionalVariableName: Option[String] = None) extends Type {

  def this(u: orc.ast.oil.named.BoundTypevar) = {
    this(u.optionalVariableName)
  }

  def this(x: TypeVariable) = {
    this(x.optionalVariableName)
  }

  override def toString = optionalVariableName getOrElse "_"

  override def <(that: Type) = (this eq that) || (super.<(that))

  override def subst(sigma: Map[TypeVariable, Type]): Type = {
    sigma.getOrElse(this, this)
  }

}
