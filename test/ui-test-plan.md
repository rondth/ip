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

### UI-1: Add, update, and list a todo

**Aim:** Verify that a todo is stored with its task type and retains its formatting when marked, unmarked, and listed.

| Step | Input |
| --- | --- |
| 1 | `todo borrow book` |
| 2 | `mark 1` |
| 3 | `list` |
| 4 | `unmark 1` |
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
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
```

#### Expected output after step 3

```text
 Here are the tasks in your list:
 1.[T][X] borrow book
____________________________________________________________
```

#### Expected output after step 4

```text
 OK, I've marked this task as not done yet:
   [T][ ] borrow book
____________________________________________________________
```

#### Expected output after step 5

```text
Bye. Hope to see you again soon!
____________________________________________________________
```
