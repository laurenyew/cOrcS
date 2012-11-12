{- SecurityExceptionTest.orc -- Orc program SecurityException
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
--resulting level is C 
--cannot write result below to higher level (no write up)
val z :: String @A = x + y
z