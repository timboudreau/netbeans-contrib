grammar test;

options {
        k = 2;
        exportVocab=MyExpr;
	buildAST = true;
}

exprlist
  : ( assignment_statement )* EOF!
  ;

assignment_statement
  : assignment SEMICOLON!
  ;

assignment
  : (IDENT ASSIGN )? expr
  ;

primary_expr
  : IDENT 
  | constant 
  | (LPAREN! expr RPAREN! ) 
  ;

sign_expr
  : (MINUS)? primary_expr
  ;

mul_expr
  : sign_expr (( TIMES | DIVIDE | MOD ) sign_expr)*
  ;

expr
  : mul_expr (( PLUS | MINUS ) mul_expr)*
  ;

constant
  : (ICON | CHCON)
  ;