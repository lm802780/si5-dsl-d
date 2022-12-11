# # SI5-DSL-D

## ArduinoML syntax validator

### Requirements

* [Python 3.9](https://www.python.org/downloads/release/python-390/)

### Install

```bash
$ pip install --user pipenv
$ pipenv install
```

### Run

```bash
$ pipenv run python main.py ./exemple/invalid/*
```

Example output:

```
$ pipenv run python main.py ./exemple/invalid/*
File:  ./exemple/invalid\syntax_error.arduinoml
ERROR l.4: Invalid brick sensore. Should be one of the following: ('sensor', 'analog sensor', 'actuator', 'buzzer')
File:  ./exemple/invalid\syntax_error_unclosed_block.arduinoml
ERROR l.12: Invalid block opening token in line 12. Close the block declared in line 1 first.
File:  ./exemple/invalid\syntax_error_unknown_state.arduinoml
ERROR l.9: Invalid state UNDEFINED. Should be one of the following: ('LOW', 'HIGH')
```