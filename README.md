# rpal_interpreter
Programming Language Group project -  Group 12 

The problem description is as follows : 

Problem Description
You are required to implement a lexical analyzer and a parser for the RPAL language. Refer 
RPAL_Lex.pdf for the lexical rules and RPAL_Grammar.pdf for the grammar details.
You should not use ‘lex’. ‘yacc’ or any such tool.
Output of the parser should be the Abstract Syntax Tree (AST) for the given input program. Then 
you need to implement an algorithm to convert the Abstract Syntax Tree (AST) in to Standardize 
Tree (ST) and implement CSE machine.
Your program should be able to read an input file which contains a RPAL program.
Output of your program should match the output of “rpal.exe“ for the relevant program. 
You must use C/C++ or Java for this project.

<b>Input and Output Requirements</b><br>
Your program should execute using the following 

For c/c++:<br>
$ ./rpal20 file_name<br>
For java:<br>
$ java rpal20 file_name<br>

Where file_name is the name of the file that has the RPAL program as the input.

<b>Input Format</b><br>
Eg: Here is one input file<br>

let Sum(A) = Psum (A,Order A )<br>
where rec Psum (T,N) = N eq 0 -> 0
 | Psum(T,N-1)+T N<br>
in Print ( Sum (1,2,3,4,5) )<br>
Output Format<br>

Output of the above program is:
15
