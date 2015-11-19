//[**]things I added to the code he provided -CA

grammar FeatherweightJavaScript;

@header { package edu.sjsu.fwjs.parser; }

// Reserved words
IF        : 'if' ;
ELSE      : 'else' ;
WHILE	  : 'while' ;		//[**]
FUNCTION  : 'function' ;	//[**]
VAR		  : 'var' ;			//[**]
PRINT	  : 'print' ;		//[**]

//LITERALS
INT       : [1-9][0-9]* | '0' ;
BOOL	  : 'true' | 'false' ;	//[**]
NULL	  : 'null' ;			//[**]

//SYMBOLS [**]
//Math Operations
SUB		  : '-' ; //subtraction
ADD		  : '+' ; //addition
MUL		  : '*' ; //multiplication
DIV		  : '/' ; //division
MOD		  : '%' ; //modulus

//Equality or Comparison [**]
GREATER		  : '>'	;  //greater than
LESSER		  : '<'	;  //less than
GREATER_EQUAL	: '>=' ; //greater than or equal
LESSER_EQUAL		: '<=' ; //less than or equal
EQUAL			: '==' ; //equals

//Utility [**]
SEPARATOR : ';' ;

// Whitespace and comments
NEWLINE   : '\r'? '\n' -> skip ;
LINE_COMMENT  : '//' ~[\n\r]* -> skip ;
BLOCK_COMMENT :	'/*' ~[a-zA-Z0-9\n\r]* '*/'	;	//[**]
WS            : [ \t]+ -> skip ; // ignore whitespace

//Identifiers [**]
VARIABLES		: [a-zA-Z_][a-zA-Z0-9_]* ;

// ***Paring rules ***
/** The start rule */
prog: stat+ ;

stat: expr SEPARATOR                            # bareExpr
    | IF '(' expr ')' block ELSE block          # ifThenElse
    | IF '(' expr ')' block                     # ifThen
    | WHILE '(' expr ')' block					# while	//[**]
    | PRINT statement							# print //[**]
    | SEPARATOR									# semicolon by itself //[**]
    ;

//[**]
expr: expr op=(MUL | DIV | MOD) expr       # MulDivMod
	| expr op=(ADD | SUB) expr			   # AddSub 
	| expr op=(GREATER | GREATER_EQUAL | LESSER | LESSER_EQUAL | EQUAL) expr	# Comparisons
	| FUNCTION expr block				   # FunctionDeclaration
	| 'f' expr							   # FunctionApplication
	| VAR VARIABLES '=' expr			   # VariableDeclaration
	| VARIABLES							   # VariableReference
	| VARIABLES '=' expr				   # AssignmentReference
    | INT                                  # int
    | BOOL								   # boolean
    | NULL								   # null
    | '(' expr ')'                         # epression inside parens
    ;

block: '{' stat* '}'                                    # fullBlock
     | stat                                             # simpBlock
     ;
