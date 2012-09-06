//
// OtherwiseGroup.scala -- Scala class OtherwiseGroup
// Project OrcScala
//
// $Id: OtherwiseGroup.scala 2896 2011-11-08 15:00:51Z jthywissen $
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

/** An OtherwiseGroup is the group associated with expression f in (f ; g)
  *
  * @author dkitchin
  */
class OtherwiseGroup(parent: Group, t: Token) extends Subgroup(parent) with Blocker {

  val quiescentWhileBlocked = true

  var state: OtherwiseGroupState = LeftSideUnknown(t)

  t.blockOn(this)

  def publish(t: Token, v: AnyRef) {
    synchronized {
      state match {
        case LeftSideUnknown(r) => { state = LeftSidePublished; r.schedule() }
        case LeftSidePublished => { /* Left side publication is idempotent */ }
        case LeftSideSilent => { throw new AssertionError("publication from silent f in f;g") }
      }
    }
    t.migrate(parent).publish(v)
  }

  def onHalt() {
    synchronized {
      state match {
        case LeftSideUnknown(r) => { state = LeftSideSilent; r.schedule() }
        case LeftSidePublished => { /* Halting after publication does nothing */ }
        case LeftSideSilent => { throw new AssertionError("publication from silent f in f;g") }
      }
    }
    parent.remove(this)
  }

  def check(t: Token) {
    synchronized {
      state match {
        case LeftSidePublished => { t.halt() }
        case LeftSideSilent => { t.unblock() }
        case LeftSideUnknown(_) => { throw new AssertionError("Spurious check") }
      }
    }
  }

}

/** Possible states of an OtherwiseGroup */
class OtherwiseGroupState
case class LeftSideUnknown(r: Token) extends OtherwiseGroupState
case object LeftSidePublished extends OtherwiseGroupState
case object LeftSideSilent extends OtherwiseGroupState
