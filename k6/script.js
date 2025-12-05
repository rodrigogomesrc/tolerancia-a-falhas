import http from 'k6/http';
import { check, sleep } from 'k6';

const SCENARIOS = {
    smoke: {
        executor: 'constant-vus',
        vus: 1,
        duration: '30s',
    },
    load: {
        executor: 'ramping-vus',
        startVUs: 0,
        stages: [
            { duration: '30s', target: 20 },
            { duration: '1m', target: 20 },
            { duration: '30s', target: 0 },
        ],
    },
    spike: {
        executor: 'ramping-vus',
        startVUs: 0,
        stages: [
            { duration: '10s', target: 100 },
            { duration: '1m', target: 0 },
        ],
    },
};

const currentScenario = __ENV.SCENARIO || 'all';

let selectedScenarios = {};

if (currentScenario === 'all') {
    selectedScenarios = {
        smoke_test: { ...SCENARIOS.smoke, startTime: '0s' },
        load_test: { ...SCENARIOS.load, startTime: '35s' },
        spike_test: { ...SCENARIOS.spike, startTime: '2m40s' },
    };
} else if (SCENARIOS[currentScenario]) {
    selectedScenarios = {
        [currentScenario]: SCENARIOS[currentScenario],
    };
} else {
    throw new Error(`Cenário '${currentScenario}' não existe.`);
}

export const options = {
    scenarios: selectedScenarios,
};

const BASE_URL = 'http://localhost:8080/buyTicket?flight=1234&day=23&user=167&ft=true';

export default function () {
    const res = http.post(BASE_URL);

    console.log('STATUS:', res.status);
    console.log('BODY:', res.body);

    check(res, {
        'status success': (r) => r.status === 200 || r.status === 201,
        'time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(1);
}