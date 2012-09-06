//
// GroupMember.scala -- Scala trait GroupMember
// Project OrcScala
//
// $Id: GroupMember.scala 2933 2011-12-15 16:26:02Z jthywissen $
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

import orc.{ Schedulable, OrcEvent }

/** @author dkitchin
  */
trait GroupMember extends Schedulable {
  def kill(): Unit
  def suspend(): Unit
  def resume(): Unit
  def notifyOrc(event: OrcEvent): Unit
}
