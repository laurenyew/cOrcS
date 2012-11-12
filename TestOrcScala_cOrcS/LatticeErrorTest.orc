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
--causes cycle error
DeclSL C [] [B]

--can't find attached level (does not exist in lattice)
val x :: Integer @ERROR = 1
x