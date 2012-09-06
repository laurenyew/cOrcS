//
// Or.java -- Java class Or
// Project OrcScala
//
// $Id: Or.java 2029 2010-08-17 15:13:38Z jthywissen $
//
// Copyright (c) 2009 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//

package orc.lib.bool;

/**
 *
 * @author dkitchin
 */
public class Or extends BoolBinopSite {

	/* (non-Javadoc)
	 * @see orc.lib.bool.BoolBinopSite#compute(boolean, boolean)
	 */
	@Override
	public boolean compute(final boolean a, final boolean b) {
		return a || b;
	}

}
