cOrcS
=====

**THESIS PROJECT**: cOrcS (Continuation of Orc Security) -- Static Type-Checking Integrity with Orc Programming Language 
(Orc is a concurrent, nondeterministic computer programming language created by Jayadev Misra at the University of Texas at Austin.

**CREATED FILES:**
src\orc\compile\securityAnalysis (static type checking step)

**EXTENSIONS** to original OrcScala project (Orc Programming Language):
Added: SecurityLevels

Functionality: 
Security Level lattice (dynamic creation and adding to);
Create/Edit a Security Level (DeclSL);
Attach a Security Level (@, HasSL) -- also checks the security level (type checking)


**NOTES:** The lattice can be created by the programmer. Cycles are not allowed (compile error is thrown if cycles exist)



**DISCLAIMER:** The OrcScala project is an ongoing project to update and provide functionality for the Orc programming language based through Scala,
and is maintained by the Orc Research Group, Professor William Cook, and Professor Jayadev Misra). The OrcScala code is open to the public and 
the cOrcS project is a thesis research project intended to extend OrcScala to include static type checking integrity for the Orc Programming language).

**Orc Programming Language Web page:** *http://orc.csres.utexas.edu/*