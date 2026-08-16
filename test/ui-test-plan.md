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

Use `<empty input>` when the user presses Enter without typing any characters. Use `<no output>` as the entire expected block when a step should produce no output. Preserve all other whitespace exactly.

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

### UI-2: Reject malformed task additions

**Aim:** Verify specific errors for todos, deadlines, and events with missing required information without terminating Henry.

| Step | Input |
| --- | --- |
| 1 | `todo` |
| 2 | `deadline homework` |
| 3 | `deadline /by Friday` |
| 4 | `deadline homework /by` |
| 5 | `event meeting` |
| 6 | `event meeting /from 2pm` |
| 7 | `event /from 2pm /to 3pm` |
| 8 | `event meeting /from /to 3pm` |
| 9 | `event meeting /from 2pm /to` |
| 10 | `bye` |

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
A deadline needs '/by'. For example: deadline submit report /by Friday
____________________________________________________________
```

#### Expected output after step 3

```text
A deadline needs a description before '/by'.
____________________________________________________________
```

#### Expected output after step 4

```text
A deadline needs a date or time after '/by'.
____________________________________________________________
```

#### Expected output after step 5

```text
An event needs '/from' and '/to'. For example: event meeting /from 2pm /to 3pm
____________________________________________________________
```

#### Expected output after step 6

```text
An event needs an ending time introduced by '/to'.
____________________________________________________________
```

#### Expected output after step 7

```text
An event needs a description before '/from'.
____________________________________________________________
```

#### Expected output after step 8

```text
An event needs a starting time after '/from'.
____________________________________________________________
```

#### Expected output after step 9

```text
An event needs an ending time after '/to'.
____________________________________________________________
```

#### Expected output after step 10

```text
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-3: Validate task numbers and empty-list commands

**Aim:** Verify empty-list output and task-number errors for both mark and unmark commands.

| Step | Input |
| --- | --- |
| 1 | `list` |
| 2 | `mark` |
| 3 | `unmark` |
| 4 | `mark first` |
| 5 | `mark 1` |
| 6 | `unmark 1` |
| 7 | `todo borrow book` |
| 8 | `mark 0` |
| 9 | `mark 2` |
| 10 | `bye` |

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
 Here are the tasks in your list:
____________________________________________________________
```

#### Expected output after step 2

```text
Please specify a task number. For example: mark 1
____________________________________________________________
```

#### Expected output after step 3

```text
Please specify a task number. For example: unmark 1
____________________________________________________________
```

#### Expected output after step 4

```text
'first' is not a valid task number.
____________________________________________________________
```

#### Expected output after step 5

```text
There are no tasks to mark yet.
____________________________________________________________
```

#### Expected output after step 6

```text
There are no tasks to unmark yet.
____________________________________________________________
```

#### Expected output after step 7

```text
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
```

#### Expected output after step 8

```text
Task 0 does not exist. Choose a number from 1 to 1.
____________________________________________________________
```

#### Expected output after step 9

```text
Task 2 does not exist. Choose a number from 1 to 1.
____________________________________________________________
```

#### Expected output after step 10

```text
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-4: Preserve state across invalid commands

**Aim:** Verify that a rejected addition and task update do not change existing tasks or completion state.

| Step | Input |
| --- | --- |
| 1 | `todo read book` |
| 2 | `deadline submit report /by Friday` |
| 3 | `mark 2` |
| 4 | `deadline missing date /by` |
| 5 | `unmark 3` |
| 6 | `list` |
| 7 | `unmark 2` |
| 8 | `list` |
| 9 | `bye` |

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
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
```

#### Expected output after step 2

```text
 Got it. I've added this task:
   [D][ ] submit report (by: Friday)
 Now you have 2 tasks in the list.
____________________________________________________________
```

#### Expected output after step 3

```text
 Nice! I've marked this task as done:
   [D][X] submit report (by: Friday)
____________________________________________________________
```

#### Expected output after step 4

```text
A deadline needs a date or time after '/by'.
____________________________________________________________
```

#### Expected output after step 5

```text
Task 3 does not exist. Choose a number from 1 to 2.
____________________________________________________________
```

#### Expected output after step 6

```text
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][X] submit report (by: Friday)
____________________________________________________________
```

#### Expected output after step 7

```text
 OK, I've marked this task as not done yet:
   [D][ ] submit report (by: Friday)
____________________________________________________________
```

#### Expected output after step 8

```text
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] submit report (by: Friday)
____________________________________________________________
```

#### Expected output after step 9

```text
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-5: Trim input and reject non-command text

**Aim:** Verify surrounding-whitespace trimming, exact command-word matching, blank-input handling, and preserved task state.

| Step | Input |
| --- | --- |
| 1 | `   todo padded task   ` |
| 2 | `todolist` |
| 3 | `<empty input>` |
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
   [T][ ] padded task
 Now you have 1 tasks in the list.
____________________________________________________________
```

#### Expected output after step 2

```text
I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
____________________________________________________________
```

#### Expected output after step 3

```text
I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
____________________________________________________________
```

#### Expected output after step 4

```text
 Here are the tasks in your list:
 1.[T][ ] padded task
____________________________________________________________
```

#### Expected output after step 5

```text
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-6: Delete and renumber tasks

**Aim:** Verify delete validation, removal confirmation, task-count updates, and list renumbering after deletion.

| Step | Input |
| --- | --- |
| 1 | `delete` |
| 2 | `delete first` |
| 3 | `delete 1` |
| 4 | `todo read book` |
| 5 | `deadline return book /by June 6th` |
| 6 | `event project meeting /from Aug 6th 2pm /to 4pm` |
| 7 | `todo join sports club` |
| 8 | `todo borrow book` |
| 9 | `mark 1` |
| 10 | `mark 2` |
| 11 | `mark 4` |
| 12 | `delete 3` |
| 13 | `list` |
| 14 | `delete 0` |
| 15 | `delete 5` |
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
Please specify a task number. For example: delete 1
____________________________________________________________
```

#### Expected output after step 2

```text
'first' is not a valid task number.
____________________________________________________________
```

#### Expected output after step 3

```text
There are no tasks to delete yet.
____________________________________________________________
```

#### Expected output after step 4

```text
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
```

#### Expected output after step 5

```text
 Got it. I've added this task:
   [D][ ] return book (by: June 6th)
 Now you have 2 tasks in the list.
____________________________________________________________
```

#### Expected output after step 6

```text
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
```

#### Expected output after step 7

```text
 Got it. I've added this task:
   [T][ ] join sports club
 Now you have 4 tasks in the list.
____________________________________________________________
```

#### Expected output after step 8

```text
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 5 tasks in the list.
____________________________________________________________
```

#### Expected output after step 9

```text
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
```

#### Expected output after step 10

```text
 Nice! I've marked this task as done:
   [D][X] return book (by: June 6th)
____________________________________________________________
```

#### Expected output after step 11

```text
 Nice! I've marked this task as done:
   [T][X] join sports club
____________________________________________________________
```

#### Expected output after step 12

```text
 Noted. I've removed this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 4 tasks in the list.
____________________________________________________________
```

#### Expected output after step 13

```text
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: June 6th)
 3.[T][X] join sports club
 4.[T][ ] borrow book
____________________________________________________________
```

#### Expected output after step 14

```text
Task 0 does not exist. Choose a number from 1 to 4.
____________________________________________________________
```

#### Expected output after step 15

```text
Task 5 does not exist. Choose a number from 1 to 4.
____________________________________________________________
```

#### Expected output after step 16

```text
Bye. Hope to see you again soon!
____________________________________________________________
```
