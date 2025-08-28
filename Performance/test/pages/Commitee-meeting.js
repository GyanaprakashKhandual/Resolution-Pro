import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    constant100: {
      executor: 'constant-vus',
      vus: 100,
      duration: '30s',     // run all 100 users for 30 seconds
      gracefulStop: '5s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],      // <1% errors
    http_req_duration: ['p(95)<1500'],   // 95% under 1.5s
  },
};

const URL = 'https://main.d1kd8ht335wv6p.amplifyapp.com/committee-meeting';

export default function () {
  const res = http.get(URL, {
    headers: {
      'User-Agent': 'k6-loadtest/1.0',
      'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
    },
    tags: { endpoint: 'meeting-page' },
  });

  check(res, {
    'status is 200': (r) => r.status === 200,
    'body not empty': (r) => r.body && r.body.length > 0,
  });

  // tiny think time to avoid ultra-tight loop; still keeps 100 concurrent users active
  sleep(1);
}
