# AI_USAGE.md

## Copilot Usage Patterns

### 1. JavaDoc & Comments
- Typing `/*` above a method triggers Copilot to suggest complete JavaDoc based on the method’s functionality.
- Typing `//` after a method signature suggests inline code snippets (e.g., Gson usage for object → JSON conversion).
- Saves time on documentation and boilerplate explanations.

### 2. POJO Generation
- Provide a JSON structure in chat → Copilot generates a POJO directly, referencing existing models.
- No need to manually create files; reduces repetitive coding effort.

### 3. Code Optimization
- Enter instructions in chat (e.g., “optimize this class”) → Copilot refactors code for readability and performance.
- Inline chat supports selecting a method → “create doc and optimize” suggestions.

### 4. Auth & Data Helpers
- Instruction given in chat to read a file and load random users → Copilot generated working code for `AuthService`.
- Demonstrates ability to scaffold complex helper logic quickly.

### 5. Build & Dependency Management
- Asked Copilot to add TestNG dependency → it edited the Gradle file correctly.
- Reduces manual lookup of dependency versions.

### 6. Code Explanation
- Select code → right‑click → “Copilot → explain/simplify” → generates human‑readable explanation.
- Useful for onboarding or knowledge transfer.

### 7. Unit Test Generation
- Right‑click on a RestController → “Copilot → create test” → generates unit tests automatically.
- Accelerates test coverage and reduces boilerplate.

### 8. Error Fixes
- Typo errors and simple compile‑time issues resolved by selecting “fix this code.”
- Acts as a quick lint + auto‑correct assistant.

---

## Review Log: What I Verified, Changed, or Rejected — and Why

### Verified
- **JavaDoc suggestions**: Accepted when they matched functionality.
- **POJO generation**: Verified fields against JSON schema; accepted when aligned with existing models.
- **Dependency edits (TestNG in Gradle)**: Verified version and scope; accepted since it matched project needs.

### Changed
- **AuthService code**: Changed recursive token fetch logic to separate initialization vs. retrieval, to avoid `StackOverflowError`.
- **Inline comments (`//`)**: Adjusted when suggestions were too verbose or not aligned with project conventions.

### Rejected
- **Overly generic JavaDoc**: Rejected when it added no value (e.g., “This method does something”).
- **Typo fixes introducing style inconsistencies**: Rejected when Copilot’s auto‑fix didn’t match team coding standards.

---

## Example: Plausible but Wrong Suggestion

**What Copilot suggested**  
It generated code in `AuthService.getAuthHeader()` that not only returned the token but also called the `signUpHelper.signUp(...)` method to fetch one. At first glance, this looked plausible — it seemed like a neat way to always ensure a fresh token.

**Why it was wrong**  
That `signUpHelper.signUp(...)` call itself went through the HTTP client, which checked for `authRequired` and called `getAuthHeader()` again. This created an infinite loop → `StackOverflowError`.

**How I caught it**
- I noticed the stack trace repeating `AuthService.getAuthHeader()` calls.
- I realized the method was doing too much: both fetching and returning the token.
- By separating initialization (`init()` to fetch token once) from retrieval (`getAuthHeader()` to just return the token), the recursion stopped.

---
