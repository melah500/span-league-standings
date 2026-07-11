---
name: project-mcp-auth-env-var
description: "GitHub MCP \"Bad credentials\" here usually means orphaned GITHUB_TOKEN env var, not a bad token"
metadata: 
  node_type: memory
  type: project
  originSessionId: 812652eb-b465-468c-a0be-a36a70b06b43
---

The GitHub MCP server (`@modelcontextprotocol/server-github`) in this project's config gets its token via the `GITHUB_TOKEN` environment variable rather than a literal value. On 2026-07-11 it failed with "Bad credentials" after a TUI crash orphaned the env var; the token itself was always valid. Relaunching the session from a shell that exports `GITHUB_TOKEN` fixed it.

**Why:** env-var indirection keeps the secret out of the repo but couples MCP auth to shell lifetime.

**How to apply:** if this MCP server returns "Bad credentials", first suspect the missing env var (ask the user to relaunch from an exporting shell) before concluding the token is expired. Also: this MCP server has no GitHub Actions tools and `gh` is not installed on this machine — the workflow badge SVG endpoint works as a rate-limit-free fallback for CI status.
