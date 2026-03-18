# Contributing

Thanks for your interest in contributing to **Electron**.

Before opening a pull request, please read this carefully.

---

## ⚠️ Contribution Status

We are **not actively accepting contributions** at this time.

You are still free to open a pull request, however:
- It may not be reviewed
- It may be closed without merging

Please keep expectations realistic.

---

## ✅ What We *Might* Accept

If you still want to contribute, your PR should be:

- **Small and focused**
- **A clear bug fix or necessary improvement**
- **Consistent with the existing codebase**

> Bug fixes are significantly more likely to be accepted than new features.

---

## ❌ What We Will Likely Reject

To save everyone time, avoid submitting:

- Large PRs with multiple unrelated changes  
- New features that were not discussed beforehand  
- Over-engineered or unnecessary abstractions  
- Massive refactors with no clear benefit  

> If your PR is big, unfocused, or unclear, it will likely be closed without review.

---

## 🧠 Code Expectations

- Follow the existing code style, do not reformat unrelated code
- Keep performance in mind (this is a PvP plugin)
- Avoid heavy operations on the main thread
- Do not introduce unnecessary complexity

---

## 🧪 Testing

Before submitting, make sure:

**The project builds successfully with:**
- Changes are tested **in game**
- Edge cases are considered (disconnects, matches, queue behaviour, etc.)

---

## 📌 Pull Request Guidelines

Your PR must include:

### What Changed
Clearly describe what you changed.

### Why
Explain the problem being solved and why your approach is correct.

### Impact
List what systems are affected (e.g. queues, kits, matches, commands).

### Testing
Confirm whether you tested or not.

---

## ✔️ Checklist

- [ ] This PR is small and focused  
- [ ] I clearly explained what changed and why  
- [ ] I tested this properly in game  
- [ ] This does not introduce unnecessary features or complexity  
