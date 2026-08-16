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
