from http.server import BaseHTTPRequestHandler, HTTPServer
import json
state = {}
class Handler(BaseHTTPRequestHandler):
    def reply(self, code, data):
        raw = json.dumps(data).encode()
        self.send_response(code)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Content-Length', str(len(raw)))
        self.end_headers()
        self.wfile.write(raw)
    def do_GET(self):
        self.reply(200, {'devices': state} if self.path == '/state' else {'error': 'not found'})
    def do_POST(self):
        if self.path != '/apply': return self.reply(404, {'error': 'not found'})
        try:
            n = int(self.headers.get('Content-Length', '0'))
            data = json.loads(self.rfile.read(n))
            state[str(data['id'])] = {'temperatureC': data['temperatureC'], 'mode': data['mode'], 'status': 'applied'}
            self.reply(200, {'status': 'applied'})
        except (ValueError, KeyError, TypeError) as exc:
            self.reply(400, {'error': str(exc)})
HTTPServer(('0.0.0.0', 8081), Handler).serve_forever()
