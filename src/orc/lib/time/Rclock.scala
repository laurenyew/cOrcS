//
// Rwait.scala -- Scala objects Rwait and Rclock, and class Rtime
// Project OrcScala
//
// $Id: Rclock.scala 2933 2011-12-15 16:26:02Z jthywissen $
//
// Created by dkitchin on Jan 13, 2011.
//
// Copyright (c) 2011 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//
package orc.lib.time

import scala.math.BigInt.int2bigInt

import orc.Handle
import orc.error.runtime.ArgumentTypeMismatchException
import orc.run.extensions.RwaitEvent
import orc.types.{ SimpleFunctionType, SignalType, RecordType, IntegerType }
import orc.values.sites.{ TypedSite, TotalSite0, Site1 }
import orc.values.OrcRecord

/**
  */
object Rclock extends TotalSite0 with TypedSite {

  def eval() = {
    new OrcRecord(
      "time" -> new Rtime(System.currentTimeMillis()),
      "wait" -> Rwait)
  }

  def orcType =
    SimpleFunctionType(
      new RecordType(
        "time" -> SimpleFunctionType(IntegerType),
        "wait" -> SimpleFunctionType(IntegerType, SignalType)))

}

/**
  */
class Rtime(startTime: Long) extends TotalSite0 {

  def eval() = (System.currentTimeMillis() - startTime).asInstanceOf[AnyRef]

}

/**
  */
object Rwait extends Site1 {

  def call(a: AnyRef, h: Handle) {
    a match {
      case delay: BigInt => {
        if (delay > 0) {
          h.notifyOrc(RwaitEvent(delay, h))
        } else if (delay == 0) {
          h.publish()
        } else {
          h.halt
        }
      }
      case _ => throw new ArgumentTypeMismatchException(0, "Integer", if (a != null) a.getClass().toString() else "null")
    }
  }

}
