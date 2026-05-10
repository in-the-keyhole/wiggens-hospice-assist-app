# Implementation Instructions

You are iteratively building a timesheet application. Read and iterate over all the OPEN Github issues for implementation requirments. Follow the instructions in the document and the Agents.md for architecture and processes.

## Repository URL

repositoryUrl = <** Codex CLI please replace this with the specifiec github URL **>



## Instructions

1. Read the Github origin/main for all open issues with the issue title having the "wiggens:" prefix in the title name. Ignore issues that do not have this prefix in the title. 
2. Loop over each issue identified for wiggens and perform the description of the issue.
3. Implement all open issues completely, following the conventions in Agents.md. Commit work after each issue loop iteration.
4. After implementing an issue in the loop, Close and comment the Github Issue .
5. **Run the relevant tests** to verify your work:
   - Backend: `cd api && ./mvnw test`
   - Frontend: `cd ui && npm test`
   - If tests fail, fix the issue before proceeding.
6. Make sure source code requiring compilation will compile without error.   
7. Create or Update a Readme.md file with developer instructions about the project and how to run, build, test, and execute the appliction
8. Follow the Github Instructions show below.

### GitHub instructions
- Loop over and implement wiggens marked github issues
- Commit after completing each issue in the loop. 
- Use conventional commit messages: `feat:`, `fix:`, `chore:`, `test:`
- Keep commits atomic — one story per commit
- Mark the Github issue closed using the Github Personel Access Token (PAT)
- Push commits to origin/master use the configured MCP github server to push. The Github Personel Access Token (PAT) defined in the GITHUB_TOKEN environment variable


## Rules
- Only work on ONE issue per iteration.
- Do not skip issues 
- Always run tests before marking an issue closed.
- If you get stuck on an issue, describe what went wrong and still attempt to fix it. Do not skip it.
- Follow all conventions in Agents.md (layered architecture, DTOs, test coverage, etc.).
- When done update or create a Readme.md with developer instructions
