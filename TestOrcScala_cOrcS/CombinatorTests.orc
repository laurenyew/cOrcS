{- CombinatorTests.orc -- Orc program CombinatorTests
 - 
 - $Id$
 - 
 - Created by LaurenYew on Nov 11, 2012 2:34:44 PM
 -
 Lattice: (everything is implicitly children of TOP and parents of BOTTOM)
 
 		TOP
 		 E
 		/ \
 	   A   B
 	  / \ /
 	 D   C
 	  \ /
 	  BOTTOM
 
 
 
 -}
DeclSL E [] [A,B]
DeclSL A [E] [D,C]
DeclSL B [] [C]
DeclSL D [] [BOTTOM]
DeclSL C [] [BOTTOM]

val x :: Integer @A = 1
val y :: Integer @B = 2
val z :: String = x + y
val b @ D = 100
val c = (z + x > a > a + 1)
c | b