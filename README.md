cOrcS
=====

Thesis Project: cOrcS (Continuation of Orc Security) -- Static Type-Checking Integrity with Orc Programming Language 

My files:
src\orc\compile\securityAnalysis (static type checking step)

My extensions to original OrcScala project (Orc Programming Language):
Adding in: SecurityLevels
Functionality: Security Level lattice (dynamic creation and adding to)
Create/Edit a Security Level (DeclSL)
Attach a Security Level (@, HasSL) -- also checks the security level (type checking)

Notes: The lattice can be created by the programmer. Cycles are not allowed (compile error is thrown if cycles exist)