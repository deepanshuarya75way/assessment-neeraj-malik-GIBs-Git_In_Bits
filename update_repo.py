import re

with open('backend/src/main/java/com/gitinbits/controller/RepoController.java', 'r') as f:
    content = f.read()

content = content.replace('import com.gitinbits.dto.context.OrgContext;', 'import com.gitinbits.dto.context.GitHubContext;')
content = re.sub(r'@RequestHeader\("X-GitHub-Org"\)\s*@NotBlank\s*String\s*org', 'GitHubContext context', content)
content = re.sub(r'log\.debug\("GET (.*?) \[org=\{\}\]"(.*?), org\);', r'log.debug("GET \1 [accountName={}]"\2, context.accountName());', content)
content = re.sub(r'new\s+OrgContext\(org\)', 'context', content)
content = re.sub(r'new\s+com\.gitinbits\.dto\.context\.RepoContext\(org,\s*repo\)', 'context, repo', content)

with open('backend/src/main/java/com/gitinbits/controller/RepoController.java', 'w') as f:
    f.write(content)
