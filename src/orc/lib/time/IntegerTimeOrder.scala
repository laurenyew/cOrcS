//
// IntegerTimeOrder.scala -- Scala object IntegerTimeOrder
// Project OrcScala
//
// $Id: IntegerTimeOrder.scala 2933 2011-12-15 16:26:02Z jthywissen $
//
// Created by dkitchin on Aug 9, 2011.
//
// Copyright (c) 2011 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//
package orc.lib.time

import orc.values.sites.TotalSite2

/** @author dkitchin
  */
object IntegerTimeOrder extends TotalSite2 {

  def eval(x: AnyRef, y: AnyRef): AnyRef = {
    // TODO: use more conventional dynamic typing
    val i = x.asInstanceOf[BigInt]
    val j = y.asInstanceOf[BigInt]
    (i compare j).asInstanceOf[AnyRef]
  }

}
