// Sample data (in real app, this would come from fetch('/API.json'))
const sampleData = [
    {
        "status": { "code": 200, "success_rate": "99%", "passed": 2460, "failed": 3 },
        "body_validation": { "check": "not_empty", "success_rate": "99%", "passed": 2460, "failed": 3 },
        "performance_metrics": {
            "http_request_duration": {
                "average": "38.33ms", "minimum": "0s", "median": "38.98ms", "maximum": "334.15ms",
                "percentile_90": "40.8ms", "percentile_95": "41.59ms"
            }
        },
        "test_configuration": {
            "page_name": "Board Meeting page",
            "page_url": "https://main.d1kd8ht335wv6p.amplifyapp.com/meeting",
            "virtual_users": 100, "test_duration": "30s"
        }
    },
    {
        "status": { "code": 200, "success_rate": "99%", "passed": 2382, "failed": 7 },
        "body_validation": { "check": "not_empty", "success_rate": "99%", "passed": 2460, "failed": 7 },
        "performance_metrics": {
            "http_request_duration": {
                "average": "43.31ms", "minimum": "0s", "median": "42.35ms", "maximum": "84.74ms",
                "percentile_90": "47.42ms", "percentile_95": "49.8ms"
            }
        },
        "test_configuration": {
            "page_name": "Home page",
            "page_url": "https://main.d1kd8ht335wv6p.amplifyapp.com",
            "virtual_users": 100, "test_duration": "30s"
        }
    },
    {
        "status": { "code": 200, "success_rate": "99%", "passed": 2468, "failed": 4 },
        "body_validation": { "check": "not_empty", "success_rate": "99%", "passed": 2468, "failed": 4 },
        "performance_metrics": {
            "http_request_duration": {
                "average": "44.46ms", "minimum": "0s", "median": "42.08ms", "maximum": "332.77ms",
                "percentile_90": "48.79ms", "percentile_95": "52.73ms"
            }
        },
        "test_configuration": {
            "page_name": "Committee Meeting page",
            "page_url": "https://main.d1kd8ht335wv6p.amplifyapp.com/committee-meeting",
            "virtual_users": 100, "test_duration": "30s"
        }
    }
];

let allData = [];
let filteredData = [];
let currentPage = 1;
const itemsPerPage = 16;

// Theme management
function initTheme() {
    const savedTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

    if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
        document.documentElement.classList.add('dark');
        document.getElementById('themeIcon').textContent = 'light_mode';
    } else {
        document.documentElement.classList.remove('dark');
        document.getElementById('themeIcon').textContent = 'dark_mode';
    }
}

function toggleTheme() {
    const isDark = document.documentElement.classList.contains('dark');
    if (isDark) {
        document.documentElement.classList.remove('dark');
        document.getElementById('themeIcon').textContent = 'dark_mode';
        localStorage.setItem('theme', 'light');
    } else {
        document.documentElement.classList.add('dark');
        document.getElementById('themeIcon').textContent = 'light_mode';
        localStorage.setItem('theme', 'dark');
    }
}

// Data loading and processing
async function loadData() {
    try {
        // In real implementation, uncomment this:
        // const response = await fetch('/API.json');
        // allData = await response.json();

        // For demo, using sample data:
        allData = sampleData;
        filteredData = [...allData];
        renderTable();
        updatePagination();
    } catch (error) {
        console.error('Error loading data:', error);
        // Fallback to sample data
        allData = sampleData;
        filteredData = [...allData];
        renderTable();
        updatePagination();
    }
}

// Table rendering
function renderTable() {
    const tableBody = document.getElementById('tableBody');
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageData = filteredData.slice(startIndex, endIndex);

    tableBody.innerHTML = '';

    pageData.forEach((item, index) => {
        const globalIndex = startIndex + index + 1;
        const row = document.createElement('tr');
        row.className = 'hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200 slide-in';

        row.innerHTML = `
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${globalIndex}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">
                        <div class="tooltip-parent relative">
                            <span class="font-medium text-blue-600 dark:text-blue-400 cursor-pointer">${item.test_configuration.page_name}</span>
                            <div class="tooltip">${item.test_configuration.page_url}</div>
                        </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.test_configuration.virtual_users}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.test_configuration.test_duration}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.performance_metrics.http_request_duration.average}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.performance_metrics.http_request_duration.minimum}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.performance_metrics.http_request_duration.median}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.performance_metrics.http_request_duration.maximum}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.performance_metrics.http_request_duration.percentile_90}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.performance_metrics.http_request_duration.percentile_95}</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${item.status.code === 200 ? 'bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100' : 'bg-red-100 text-red-800 dark:bg-red-800 dark:text-red-100'}">
                            ${item.status.code === 200 ? 'Success' : 'Failed'}
                        </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.status.success_rate}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">${item.body_validation.success_rate}</td>
                `;

        tableBody.appendChild(row);
    });
}

// Pagination
function updatePagination() {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const startItem = (currentPage - 1) * itemsPerPage + 1;
    const endItem = Math.min(currentPage * itemsPerPage, filteredData.length);

    document.getElementById('paginationInfo').textContent =
        `Showing ${startItem}-${endItem} of ${filteredData.length} results`;

    document.getElementById('prevBtn').disabled = currentPage === 1;
    document.getElementById('nextBtn').disabled = currentPage === totalPages || totalPages === 0;

    const pageNumbers = document.getElementById('pageNumbers');
    pageNumbers.innerHTML = '';

    for (let i = 1; i <= totalPages; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.className = `px-3 py-1 rounded text-sm transition-colors duration-300 ${i === currentPage
                ? 'bg-blue-600 text-white'
                : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
            }`;
        pageBtn.textContent = i;
        pageBtn.onclick = () => goToPage(i);
        pageNumbers.appendChild(pageBtn);
    }
}

function goToPage(page) {
    currentPage = page;
    renderTable();
    updatePagination();
}

function nextPage() {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        renderTable();
        updatePagination();
    }
}

function prevPage() {
    if (currentPage > 1) {
        currentPage--;
        renderTable();
        updatePagination();
    }
}

// Search functionality
function filterData(searchTerm) {
    if (!searchTerm) {
        filteredData = [...allData];
    } else {
        filteredData = allData.filter(item =>
            item.test_configuration.page_name.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }
    currentPage = 1;
    renderTable();
    updatePagination();
}

// Event listeners
document.addEventListener('DOMContentLoaded', function () {
    initTheme();
    loadData();

    document.getElementById('themeToggle').addEventListener('click', toggleTheme);
    document.getElementById('docButton').addEventListener('click', () => {
        window.location.href = 'doc.html';
    });

    document.getElementById('searchInput').addEventListener('input', (e) => {
        filterData(e.target.value);
    });

    document.getElementById('prevBtn').addEventListener('click', prevPage);
    document.getElementById('nextBtn').addEventListener('click', nextPage);
});