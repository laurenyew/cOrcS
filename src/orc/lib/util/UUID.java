//
// UUID.java -- Java class UUID
// Project OrcScala
//
// $Id: UUID.java 2341 2011-01-15 17:06:23Z jthywissen $
//
// Copyright (c) 2009 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//

package orc.lib.util;

import orc.error.runtime.TokenException;
import orc.types.Type;
import orc.values.sites.TypedSite;
import orc.values.sites.compatibility.Args;
import orc.values.sites.compatibility.EvalSite;
import orc.values.sites.compatibility.Types;

/**
 * Generate random UUIDs.
 * @author quark
 */
public class UUID extends EvalSite implements TypedSite {
	@Override
	public Object evaluate(final Args args) throws TokenException {
		return java.util.UUID.randomUUID().toString();
	}

	@Override
	public Type orcType() {
		return Types.function(Types.string());
	}
}
