# Console UI Test Plan

This file is the source of truth for `$test-ui`. Run each case in a fresh program process and stop the full test session at the first failure.

## Test case template

### UI-N: Short descriptive name

**Aim:** Describe the behavior this case verifies.

| Step | Input |
| --- | --- |
| 1 | `command` |
| 2 | `bye` |

#### Expected startup output

```text
Paste the exact output printed before the first input here.
```

#### Expected output after step 1

```text
Paste the exact output caused by step 1 here.
```

#### Expected output after step 2

```text
Paste the exact output caused by step 2 here.
```

Use `<no output>` as the entire expected block when a step should produce no output. Preserve all other whitespace exactly.

### UI-1: Add and list all task types

**Aim:** Verify that todos, deadlines, and events are stored and displayed using their type-specific formats.

| Step | Input |
| --- | --- |
| 1 | `todo borrow book` |
| 2 | `deadline do homework /by no idea :-p` |
| 3 | `event project meeting /from Mon 2pm /to 4pm` |
| 4 | `list` |
| 5 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hello! I'm Henry.
What can I do for you?
____________________________________________________________
```

#### Expected output after step 1

```text
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
```

#### Expected output after step 2

```text
 Got it. I've added this task:
   [D][ ] do homework (by: no idea :-p)
 Now you have 2 tasks in the list.
____________________________________________________________
```

#### Expected output after step 3

```text
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
```

#### Expected output after step 4

```text
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] do homework (by: no idea :-p)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
```

#### Expected output after step 5

```text
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-2: Recognize a command without arguments

**Aim:** Verify that a bare command word is recognized and reported as missing information.

| Step | Input |
| --- | --- |
| 1 | `todo` |
| 2 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hello! I'm Henry.
What can I do for you?
____________________________________________________________
```

#### Expected output after step 1

```text
A todo needs a description. For example: todo borrow a book
____________________________________________________________
```

#### Expected output after step 2

```text
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-3: Reject malformed commands and continue

**Aim:** Verify specific errors for malformed task, mark, and unknown commands without terminating Henry.

| Step | Input |
| --- | --- |
| 1 | `deadline homework` |
| 2 | `deadline /by Friday` |
| 3 | `deadline homework /by` |
| 4 | `event meeting` |
| 5 | `event meeting /from 2pm` |
| 6 | `event /from 2pm /to 3pm` |
| 7 | `event meeting /from /to 3pm` |
| 8 | `event meeting /from 2pm /to` |
| 9 | `mark` |
| 10 | `mark first` |
| 11 | `mark 1` |
| 12 | `todo borrow book` |
| 13 | `mark 2` |
| 14 | `unmark 0` |
| 15 | `blah` |
| 16 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hello! I'm Henry.
What can I do for you?
____________________________________________________________
```

#### Expected output after step 1

```text
A deadline needs '/by'. For example: deadline submit report /by Friday
____________________________________________________________
```

#### Expected output after step 2

```text
A deadline needs a description before '/by'.
____________________________________________________________
```

#### Expected output after step 3

```text
A deadline needs a date or time after '/by'.
____________________________________________________________
```

#### Expected output after step 4

```text
An event needs '/from' and '/to'. For example: event meeting /from 2pm /to 3pm
____________________________________________________________
```

#### Expected output after step 5

```text
An event needs an ending time introduced by '/to'.
____________________________________________________________
```

#### Expected output after step 6

```text
An event needs a description before '/from'.
____________________________________________________________
```

#### Expected output after step 7

```text
An event needs a starting time after '/from'.
____________________________________________________________
```

#### Expected output after step 8

```text
An event needs an ending time after '/to'.
____________________________________________________________
```

#### Expected output after step 9

```text
Please specify a task number. For example: mark 1
____________________________________________________________
```

#### Expected output after step 10

```text
'first' is not a valid task number.
____________________________________________________________
```

#### Expected output after step 11

```text
There are no tasks to mark yet.
____________________________________________________________
```

#### Expected output after step 12

```text
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
```

#### Expected output after step 13

```text
Task 2 does not exist. Choose a number from 1 to 1.
____________________________________________________________
```

#### Expected output after step 14

```text
Task 0 does not exist. Choose a number from 1 to 1.
____________________________________________________________
```

#### Expected output after step 15

```text
I don't recognise that command. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
```

#### Expected output after step 16

```text
Bye. Hope to see you again soon!
____________________________________________________________
```
