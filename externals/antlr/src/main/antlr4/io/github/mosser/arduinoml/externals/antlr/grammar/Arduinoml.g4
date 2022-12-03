grammar Arduinoml;


/******************
 ** Parser rules **
 ******************/

root            :   declaration bricks states EOF;

declaration     :   'application' name=IDENTIFIER;

bricks          :   (sensor|actuator)+;
    sensor      :   'sensor'   location ;
    actuator    :   'actuator' location ;
    location    :   id=IDENTIFIER ':' port=PORT_NUMBER;

states          :   state+;
    state       :   initial? name=IDENTIFIER '{'  (action* action*) (transition | transitionSleep | transitionCondition) '}';
    action      :   actionable+ '<=' value=SIGNAL;
        actionable: receiver=IDENTIFIER;
    transition  :   trigger=IDENTIFIER 'is' value=SIGNAL '=>' next=IDENTIFIER;
    transitionCondition  :  trigger1=IDENTIFIER 'is' value=SIGNAL(condition)* '=>' next=IDENTIFIER;
        condition: connector=CONNECTOR trigger=IDENTIFIER 'is' value=SIGNAL ;
    transitionSleep      :  timeInMillis=INTEGER 'ms' '=>' next=IDENTIFIER;
    initial     :   '->';

/*****************
 ** Lexer rules **
 *****************/

PORT_NUMBER     :   [1-9] | '11' | '12';
IDENTIFIER      :   LOWERCASE (LOWERCASE|UPPERCASE)+;
SIGNAL          :   'HIGH' | 'LOW';
CONNECTOR       :   'AND' | 'OR';
INTEGER         :   DIGIT+;

/*************
 ** Helpers **
 *************/

fragment LOWERCASE  : [a-z];                                 // abstract rule, does not really exists
fragment UPPERCASE  : [A-Z];
fragment DIGIT      : ('0'..'9');
NEWLINE             : ('\r'? '\n' | '\r')+      -> skip;
WS                  : ((' ' | '\t')+)           -> skip;     // who cares about whitespaces?
COMMENT             : '#' ~( '\r' | '\n' )*     -> skip;     // Single line comments, starting with a #
