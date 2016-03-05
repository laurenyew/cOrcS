**cOrcs (Continuation of Orc Security)** (2011-2012). Designed Scala language compiler and interpreter rules with new Orc grammar options for an optional security extension feature.  Scala, Orc

**Undergraduate Honors Thesis** (2013): Programming Language Security with Orc and Scala
Turing Scholars Honors Program (Computer Science) at the University of Texas at Austin
*	Supervising professors: William D. Young, Ph.D., William R. Cook, Ph.D.
*	Sample code: https://github.com/laurenyew/cOrcS.git
*	Thesis paper: http://apps.cs.utexas.edu/tech_reports/reports/tr/TR-2113.pdf

===
**THESIS PROJECT**: Static Checking Integrity with Orc Programming Language 
(Orc is a concurrent, nondeterministic computer programming language created by Jayadev Misra at the University of Texas at Austin.

**CREATED FILES:**
src\orc\compile\securityAnalysis (static integrity checking step) &
TestOrcScala_cOrcS (demos for thesis presentation on uses of cOrcS)

**EXTENSIONS** to original OrcScala project (Orc Programming Language):
Added: SecurityLevels

Functionality: 
Security Level lattice (dynamic programmer creation and edit);
Create/Edit a Security Level (DeclSL);
Attach a Security Level (@, HasSL) -- also checks the security level (checking)

Biba Strict Integrity

**NOTES:** The lattice can be created by the programmer. Cycles are not allowed (compile error is thrown if cycles exist)



**DISCLAIMER:** The OrcScala project is an ongoing project to update and provide functionality for the Orc programming language based through Scala,
and is maintained by the Orc Research Group, Professor William Cook, and Professor Jayadev Misra). The OrcScala code is open to the public and 
the cOrcS project is a thesis research project intended to extend OrcScala to include static checking integrity for the Orc Programming language).

**Orc Programming Language Web page:** *http://orc.csres.utexas.edu/*
