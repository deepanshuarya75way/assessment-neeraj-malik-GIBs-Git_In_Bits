import urllib.request
import urllib.error

url = 'http://localhost:9090/api/teams'
req = urllib.request.Request(url, headers={
    'X-GitHub-Source-Type': 'PUBLIC_REPOSITORY',
    'X-GitHub-Source-Value': 'spring-projects/spring-framework'
})

try:
    with urllib.request.urlopen(req) as response:
        print('Status:', response.status)
        print('Body:', response.read().decode('utf-8'))
except urllib.error.HTTPError as e:
    print('HTTPError:', e.code)
    print('Body:', e.read().decode('utf-8'))
except Exception as e:
    print('Error:', str(e))
