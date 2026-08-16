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
