# Spring Security — Key Concepts

---

## 1. @PreAuthorize("hasRole('ADMIN')")

### What is it?
A **method-level lock**. It runs right before the controller method executes.

### Real-World Analogy
Think of a bank vault. Even if you passed the outer security gate, the vault itself has another lock that requires a specific key.

### How It Works
- It checks the user's **role** from the `SecurityContext` (set by `JwtFilter`)
- `hasRole('ADMIN')` means: *"Does this user have the authority `ROLE_ADMIN`?"*
- **Yes** → method runs
- **No** → Access Denied (403 Forbidden)

### Example in Your Code
```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/create-manager")
public String createManager(@RequestBody RegisterRequest request) {
    return authService.createUserByAdmin(request, UserRole.MANAGER);
}
```

### Scenario: CUSTOMER tries to access admin endpoint

```
Step 1: JwtFilter validates JWT token → User is authenticated
Step 2: @PreAuthorize checks SecurityContext → Role is CUSTOMER
Step 3: CUSTOMER != ADMIN → Access Denied
Result: 403 Forbidden
```

---

## 2. HttpSecurity Object

### What is it?
A **builder/factory object** used to configure security rules step by step.

### Analogy
Think of it as a **settings panel** with toggle switches:

```java
HttpSecurity http  // The settings panel

http
    .csrf(...)         // Switch 1: Disable CSRF
    .authorizeHttpRequests(...) // Switch 2: Public vs Private paths
    .addFilterBefore(...)       // Switch 3: Add JWT filter
    .formLogin(...)    // Switch 4: Disable form login
    .httpBasic(...)    // Switch 5: Disable basic auth
```

You configure each switch, then `.build()` turns it into a real `SecurityFilterChain`.

---

## 3. SecurityFilterChain Object

### What is it?
A **list of security filters** that every incoming request must pass through, in order.

### Analogy: Airport Security Walkway

```
You (Request)
  → Security Guard 1 (Check ticket)
  → Security Guard 2 (Scan bag)
  → Security Guard 3 (Check ID)
  → Board plane (Reach Controller)
```

Each "security guard" is a **filter**. The entire walkway is the `SecurityFilterChain`.

### In Your App, Key Filters Are:
1. `JwtFilter` (YOUR guard — checks JWT badge)
2. `UsernamePasswordAuthenticationFilter` (SPRING'S built-in guard)

---

## 4. auth -> auth (Lambda Syntax)

### What is it?
Modern Java lambda shorthand. Both versions do exactly the same thing.

### Without Lambda (Verbose)
```java
.authorizeHttpRequests(new Consumer<AuthorizeHttpRequestsConfigurer<...>>() {
    public void accept(AuthorizeHttpRequestsConfigurer<...> auth) {
        auth.requestMatchers("/auth/**").permitAll();
        auth.anyRequest().authenticated();
    }
})
```

### With Lambda (Your Code — Cleaner)
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**").permitAll()
    .anyRequest().authenticated()
)
```

---

## 5. Why JwtFilter Runs BEFORE UsernamePasswordAuthenticationFilter?

### The Filter Chain Order
```
Request comes in
  → JwtFilter (YOUR guard)          ← Runs FIRST
  → UsernamePasswordAuthenticationFilter (SPRING'S guard) ← Runs SECOND
  → Controller
```

### What Each Filter Does

**JwtFilter (Your Custom Guard):**
1. Reads `Authorization: Bearer <token>` header
2. Validates the JWT
3. **Sets user identity in SecurityContext**

**UsernamePasswordAuthenticationFilter (Spring's Built-in Guard):**
1. Only works for form login or Basic Auth
2. Since you disabled both, it mostly just checks if SecurityContext already has authentication

### Why Order Matters — RIGHT Order (Your Code)

```
Request → JwtFilter validates token → Sets SecurityContext → Spring Filter sees auth → Controller
```

**JwtFilter runs FIRST and sets the user's identity. When Spring's filter runs next, it sees the user is already authenticated and allows the request through.**

### What Happens If Order Is REVERSED

```
Request → Spring Filter (sees NO auth) → REJECTED → JwtFilter NEVER runs
```

**If JwtFilter ran after Spring's filter, Spring's filter would reject every request before JWT validation even happened.**

### How to Confirm This in Your Code

```java
// SecurityConfig.java line 32
.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
```

This explicitly tells Spring: *"Insert JwtFilter BEFORE UsernamePasswordAuthenticationFilter."*

---

## 6. permitAll() vs authenticated()

### permitAll()
Allows access **without** any token or authentication.
```java
.requestMatchers("/auth/**").permitAll()  // Register and Login
```

### authenticated()
Requires a **valid JWT token**. Without it, request is rejected.
```java
.anyRequest().authenticated()  // Admin, Orders, etc.
```

### Real-World Example
```
/auth/**  = Public lobby (anyone can enter)
/admin/** = Locked floors (need ID badge)
/courier/** = Locked floors (need ID badge)
```
