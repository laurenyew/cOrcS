{- SiteTEst.orc -- Orc program SiteTest
 - 
 - $Id$ a + b > x > x + c
 - 
 - Created by LaurenYew on June 24, 2012 2:34:44 PM

 -}
DeclSL A [] []
DeclSL B [] []
DeclSL C [A,B] []
DeclSL D [C] []
DeclSL E [] []
DeclSL B [] []

val z :: Integer @ A = 1 
val y :: Integer @ C = 2 + z
val d = 3 + y + 2
val e :: Boolean @ E = true

--Note: result is overall level (no read down). 
--Println is TOP level by default
Println("d is: " + (2 + d) + " and e is " + e)

--Show site error with null (no level) site trying to possibly modify value
--def compute (x) = x 
--compute(e)

{- Examples of site calls -}

--Prompt("What is your name?")

-- Println("Hello World")

-- 2 + 3

-- true && false