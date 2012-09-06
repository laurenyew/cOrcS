//
// Subgroup.scala -- Scala class Subgroup
// Project OrcScala
//
// $Id: Subgroup.scala 2933 2011-12-15 16:26:02Z jthywissen $
//
// Created by dkitchin on Aug 12, 2011.
//
// Copyright (c) 2011 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//
package orc.run.core

import orc.{ OrcEvent, CaughtEvent }

/** @author dkitchin
  */
abstract class Subgroup(parent: Group) extends Group {

  override val runtime = parent.runtime

  override val root = parent.root

  parent.add(this)

  override def kill() = synchronized { super.kill(); parent.remove(this) }

  def notifyOrc(event: OrcEvent) = parent.notifyOrc(event)

  def run() {
    try {
      if (parent.isKilled()) { kill() }
    } catch {
      case e: InterruptedException => Thread.currentThread().interrupt()
      case e => { notifyOrc(CaughtEvent(e)) }
    }
  }

}
