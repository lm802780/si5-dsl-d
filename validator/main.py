import re
import glob

from typing import List, Dict, Any


ParserResultType = List[Dict[str, Any]]
BrickType = Dict[str, Dict[str, str]]

valid_as_brick = ('sensor', 'analog sensor', 'actuator', 'buzzer')
valid_as_state = ('LOW', 'HIGH')
valid_as_condition = ('is',)
valid_as_active_state = ('on', 'off')


def state_block_validator(block_lines: List[str], line_index: int, result: ParserResultType, bricks: BrickType):
    start_block_regex = re.compile('(\w+|\-\> \w+) {')
    set_regex = re.compile('(\w+) <= (\w+)')
    goto_state_regex = re.compile('(\w+) (\w+) (\w+) => (\w+)')
    is_inside_block = False
    for i, line in enumerate(block_lines, start=line_index):
        if not is_inside_block:
            m = start_block_regex.match(line)
            if not m:
                result.append({'line': i, 'error': f"Block opening token expected in line {i}. Got instead {line}"})
                return
            is_inside_block = True
        else:
            m = set_regex.match(line.strip())
            if m:
                if not line.startswith(' ' * 4):
                    result.append({'line': i, 'error': f"Invalid indent in line {line}."})
                    return
                if m.group(1) not in bricks:
                    result.append({'line': i, 'error': f"Unknown brick {m.group(1)}. Should be one of the following: {list(bricks.keys())}"})
                    return
                if m.group(2) not in valid_as_state:
                    result.append({'line': i, 'error': f"Invalid state {m.group(2)}. Should be one of the following: {valid_as_state}"})
                    return
                continue
            m = goto_state_regex.match(line.strip())
            if m:
                if not line.startswith(' ' * 4):
                    result.append({'line': i, 'error': f"Invalid indent in line {line}."})
                    return
                if m.group(1) not in bricks:
                    result.append({'line': i, 'error': f"Unknown brick {m.group(1)}. Should be one of the following: {list(bricks.keys())}"})
                    return
                if m.group(2) not in valid_as_condition:
                    result.append({'line': i, 'error': f"Invalid condition {m.group(2)}. Should be one of the following: {valid_as_condition}"})
                    return
                if m.group(3) not in valid_as_state:
                    result.append({'line': i, 'error': f"Invalid state {m.group(3)}. Should be one of the following: {valid_as_state}"})
                    return
                if m.group(4) not in valid_as_active_state:
                    result.append({'line': i, 'error': f"Invalid new state {m.group(3)}. Should be one of the following: {valid_as_active_state}"})
                    return
                continue
            if not line.strip():
                result.append({'line': i, 'warning': "Unecessary blank line."})
                continue
            if line.strip() != '}':
                result.append({'line': i, 'error': f"Invalid token in line {line.strip()}."})
            return


def states_validator(lines: List[str], line_index: int, result: ParserResultType, bricks: BrickType):
    is_inside_block = False
    blocks_indexes = []
    for i, line in enumerate(lines, start=line_index):
        if line.strip().endswith('{'):
            if len(blocks_indexes) % 2 != 0:
                result.append({'line': i, 'error': f"Invalid block opening token in line {i}. Close the block declared in line {blocks_indexes[-1] + 1} first."})
                return result
            blocks_indexes.append(i - line_index)
        elif line.strip().endswith('}'):
            if len(blocks_indexes) % 2 == 0:
                result.append({'line': i, 'error': f"Invalid block closing token in line {i}."})
                return result
            blocks_indexes.append(i - line_index)
        else:
            continue
    if not blocks_indexes:
        result.append({'line': i, 'error': f"Missing a transition block."})
        return result
    if len(blocks_indexes) % 2 != 0:
        result.append({'line': i, 'error': f"Invalid unclosed block in line {i}."})
        return result
    for e in range(0, len(blocks_indexes), 2):
        state_block_validator(lines[blocks_indexes[e]:blocks_indexes[e+1]+1], blocks_indexes[e] + line_index + 1, result, bricks)
    return result


def bricks_validator(lines: List[str], line_index: int, result: ParserResultType):
    brick_regex = re.compile('(\w+) (\w+): (\d{1,2})')
    is_inside_bricks_block = False
    bricks = {}
    for i, line in enumerate(lines, start=line_index):
        if line.strip() == "# Declaring bricks":
            is_inside_bricks_block = True
        elif not line.strip():
            continue
        elif line.strip() == "# Declaring states":
            return lines[i - 1:], i, result, bricks
        elif is_inside_bricks_block:
            if not line.strip():
                result.append({'line': i, 'warning': "Unecessary blank line."})
                continue
            m = brick_regex.match(line)
            if not m:
                result.append({'line': i, 'error': f"Invalid token in line {line.strip()}."})
                return lines[i:], i + 1, result, bricks
            if m.group(1) not in valid_as_brick:
                result.append({'line': i, 'error': f"Invalid brick {m.group(1)}. Should be one of the following: {valid_as_brick}"})
                return lines[i:], i + 1, result, bricks
            bricks[m.group(2)] = {'type': m.group(1), 'value': m.group(3)}
        else:
            result.append({'line': i, 'error': f"Invalid token {line.strip()[0]}."})
            return lines[i:], i + 1, result, bricks
    return lines[i:], i + 1, result, bricks



def application_validator(lines: List[str], result: ParserResultType):
    for i, line in enumerate(lines, start=1):
        if line.strip() and not line.strip().startswith('application'):
            result.append({'line': i, 'error': f"Expecting line starting with 'application'. Got {line.split()[0]} instead."})
            break 
        elif line.strip() and line.strip().startswith('application'):
            break
        else:
            result.append({'line': i, 'warning': "Unecessary blank line."})
    return lines[i:], i + 1, result


for filepath in glob.glob('validator/exemple/valid/*'):
    print("File: ", filepath)
    with open(filepath) as f:
        events = states_validator(*bricks_validator(*application_validator(f.readlines(), [])))
        for event in events:
            print(f"{('error' if 'error' in event else 'warning').upper()} l.{event['line']}: {event['error'] if 'error' in event else event['warning']}")
            if 'error' in event:
                break

for filepath in glob.glob('validator/exemple/invalid/*'):
    print("File: ", filepath)
    with open(filepath) as f:
        events = states_validator(*bricks_validator(*application_validator(f.readlines(), [])))
        for event in events:
            print(f"{('error' if 'error' in event else 'warning').upper()} l.{event['line']}: {event['error'] if 'error' in event else event['warning']}")
            if 'error' in event:
                break
