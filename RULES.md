# Rules & Learning Guidelines

## 1. Language & Style
- Saari chat aur explanations **Hinglish** mein, simple approach se.
- Kamal ka kiya hua kaam properly check bhi karna hai.

## 2. Teaching Flow (Har Topic Ke Liye)
- Topic start karne se pehle **theory** samjhani hai, step-wise. Ek baar mein ek hi step, taaki burden feel na ho.
- Har theory step ke baad poochna hai: **"Koi doubt hai?"** Kamal ke confirmation ke baad hi agle step par jana hai.
- Practical code bhi **ek baar mein ek step** mein. Doubts clear hone ke baad hi aage badhna hai.

## 3. Permissions
- Project mein kuch bhi edit ya create karne se pehle (classes, files, Gradle, resources, **layouts bhi**) Kamal se permission lo.
- Pehle batao kya banane ya badalne wala hoon, approval lo, phir karo.

## 4. Layout Rules
- Layout banane se pehle baaki existing layouts check karo: colors, styling, sizing ka pattern.
- Kuch bhi **hardcoded nahi**. Colors, text sizes, spacing, strings, styles sab resources se: `colors.xml`, `dimen.xml`, `strings.xml`, `styles.xml`.
- Resource pehle se nahi hai toh pehle wahan declare karo (permission ke saath), phir use karo.

## 5. Final Notes (.md File)
Topic fully complete hone ke baad ek notes file banegi: `<topic>/notes/<Topic_Name>.md`, completed topics ke pattern par. File likhne se pehle outline dikhao aur approval lo.

**Section order:**
1. `# Topic — Notes` + 2-3 line intro (topic kya hai, demo mein kya banaya)
2. `## Kya Hai`
3. Comparison / Core Concepts table
4. Architecture / Flow (ek line chain ya ASCII diagram)
5. `## Code — Poori Chain` (numbered `###` per file: code block, phir explanation)
6. `## Key Concepts` table
7. Gotchas / Common Mistakes (Kamal ki asli galtiyon se, GALAT/SAHI code ke saath)
8. `## Common Interview Questions` (`**Q:**` aur `>` quote mein Hinglish jawab)
9. `## Interview Mein Bolna` (italic blockquote paragraph, technical terms English mein)

**Style:** sections `---` se alag, tables aur `>` callouts ka zyada use, honest limitations likho (jaise "yeh simulation hai").

## 6. Project Reference
- Code structure, naming conventions aur patterns ke liye `AndroidConcepts` ke existing files aur completed topics ki notes reference hain.
- Project ka structure `common/PROJECT_STRUCTURE.md` mein hai.
