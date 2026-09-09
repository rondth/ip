# Henry User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

Use `deadline DESCRIPTION /by DATE` to add a task with a due date. Henry stores the date as a
date and time, rather than as plain text.

Accepted date formats are `d/M/yyyy HHmm` (for example, `2/12/2019 1800`) and `yyyy-MM-dd`
(for example, `2019-12-02`). A date without a time is due at the start of that day.

Example: `deadline return book /by 2/12/2019 1800`

Henry displays the deadline in a more readable form:

```
[D][ ] return book (by: Dec 2 2019 6:00 PM)
```

## Using command aliases

Henry accepts the following shorter aliases for its commands:

| Command | Alias |
| --- | --- |
| `todo` | `t` |
| `deadline` | `d` |
| `event` | `e` |
| `list` | `l` |
| `find` | `f` |
| `mark` | `m` |
| `unmark` | `u` |
| `delete` | `del` |
| `bye` | `b` |

Aliases use the same arguments as the full commands. For example, `t borrow book` works like
`todo borrow book`, and `m 1` works like `mark 1`. Aliases are lowercase and must be entered as a
complete first word.

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
