grammar Arduinoml;


/******************
 ** Parser rules **
 ******************/

root            :   declaration bricks states EOF;

declaration     :   'application' name=IDENTIFIER;

bricks          :   (sensor|analogSensor|actuator)+;
    sensor      :   'sensor'   location ;
    analogSensor:   'analog sensor'   location ;
    actuator    :   'actuator' location ;
    location    :   id=IDENTIFIER ':' port=PORT_NUMBER;

states          :   state+;
    state       :   initial? name=IDENTIFIER '{'  action* transition '}';
    action      :   actionable+ '<=' value=SIGNAL;
        actionable: receiver=IDENTIFIER;
    transition: (digitalTransition|analogTransition|sleepTransition)+;
        digitalTransition  :   (condition)* '=>' next=IDENTIFIER;
            condition: trigger=IDENTIFIER 'is' value=SIGNAL (connector=CONNECTOR)?  ;
        analogTransition  :   (conditionA)* '=>' next=IDENTIFIER;
            conditionA : trigger=IDENTIFIER infsup=INFSUP value=NUMBER (connector=CONNECTOR)?;
        sleepTransition      :  timeInMillis=NUMBER 'ms' '=>' next=IDENTIFIER;
    initial     :   '->';

/*****************
 ** Lexer rules **
 *****************/

PORT_NUMBER     :   [1-9] | '11' | '12' | 'A0'| 'A1';
IDENTIFIER      :   LOWERCASE (LOWERCASE|UPPERCASE)+;
SIGNAL          :   'HIGH' | 'LOW';
CONNECTOR       :   'AND';
INFSUP       :   'ABOVE' | 'BELOW';
NUMBER       :   [0-9]+;

/*************
 ** Helpers **
 *************/

fragment LOWERCASE  : [a-z];                                 // abstract rule, does not really exists
fragment UPPERCASE  : [A-Z];
NEWLINE             : ('\r'? '\n' | '\r')+      -> skip;
WS                  : ((' ' | '\t')+)           -> skip;     // who cares about whitespaces?
COMMENT             : '#' ~( '\r' | '\n' )*     -> skip;     // Single line comments, starting with a #
