//
// Floor.java -- Java class Floor
// Project OrcScala
//
// $Id: Floor.java 2541 2011-03-14 22:38:31Z jthywissen $
//
// Copyright (c) 2009 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//

package orc.lib.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import orc.error.runtime.TokenException;
import orc.types.Type;
import orc.values.sites.TypedSite;
import orc.values.sites.compatibility.Args;
import orc.values.sites.compatibility.EvalSite;
import orc.values.sites.compatibility.Types;

public class Floor extends EvalSite implements TypedSite {

	public static BigInteger floor(BigDecimal d) {
		if (d.signum() >= 0) {
			return d.toBigInteger();
		} else {
			return Ceil.ceil(d.negate()).negate();
		}
	}

	@Override
	public Object evaluate(final Args args) throws TokenException {
		final Number n = args.numberArg(0);
		if (   n instanceof BigInteger
			|| n instanceof Integer
			|| n instanceof Long
			|| n instanceof Short
			|| n instanceof Byte) {
			return n;
		} else {
			BigDecimal d = (n instanceof BigDecimal ? (BigDecimal) n : new BigDecimal(n.doubleValue()));
			return floor(d);
		}
	}

	@Override
	public Type orcType() {
		return Types.function(Types.number(), Types.integer());
	}
}
