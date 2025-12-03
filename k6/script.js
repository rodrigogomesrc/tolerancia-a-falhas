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

// Lógica para selecionar o cenário via variável de ambiente
// Ex: k6 run -e SCENARIO=smoke script.js
const currentScenario = __ENV.SCENARIO || 'all';

let selectedScenarios = {};

if (currentScenario === 'all') {
    // Se for 'all', configura para rodar um após o outro usando startTime
    selectedScenarios = {
        smoke_test: { ...SCENARIOS.smoke, startTime: '0s' },
        load_test: { ...SCENARIOS.load, startTime: '35s' }, // 30s do smoke + 5s folga
        spike_test: { ...SCENARIOS.spike, startTime: '2m40s' }, // Tempo acumulado anterior
    };
} else if (SCENARIOS[currentScenario]) {
    // Se escolheu um específico, roda apenas ele
    selectedScenarios = {
        [currentScenario]: SCENARIOS[currentScenario],
    };
} else {
    throw new Error(`Cenario '${currentScenario}' não encontrado. Use: smoke, load, spike ou não passe nada.`);
}

const BASE_URL = 'http://localhost:8080/buyTicket?flight=1234&day=23&user=167&ft=true';

export default function () {

    /*
    const payload = generateRandomPayload();
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'User-Agent': 'K6-LoadTest/1.0',
        },
    };

     */

    //const res = http.post(BASE_URL, payload, params);
    const res = http.post(BASE_URL);

    console.log('STATUS:', res.status);
    console.log('BODY:', res.body);

    check(res, {
        'status success': (r) => r.status === 200 || r.status === 201,
        'time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(1);
}