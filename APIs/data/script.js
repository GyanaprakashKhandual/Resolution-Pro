class APIDashboard {
    constructor() {
        this.apis = [];
        this.filteredApis = [];
        this.currentPage = 1;
        this.itemsPerPage = 6;
        this.currentFilter = "all";
        this.searchQuery = "";
        this.expandedCards = new Set();

        this.init();
    }

    async init() {
        await this.loadData();
        this.setupEventListeners();
        this.populateFilters();
        this.renderAPIs();
    }

    async loadData() {
        try {
            const response = await fetch("https://avidus-interactive-test-data.onrender.com/api/resolution-pro");
            this.apis = await response.json();
            this.filteredApis = [...this.apis];
            document.getElementById("loading").classList.add("hidden");
            document.getElementById("apiContainer").classList.remove("hidden");
        } catch (error) {
            console.error("Failed to load API data:", error);
            this.filteredApis = [...this.apis];
            document.getElementById("loading").classList.add("hidden");
            document.getElementById("apiContainer").classList.remove("hidden");
        }
    }

    setupEventListeners() {
        // Search functionality
        document.getElementById("searchInput").addEventListener("input", (e) => {
            this.searchQuery = e.target.value.toLowerCase();
            this.filterAPIs();
        });

        // Filter dropdown
        document.getElementById("filterBtn").addEventListener("click", () => {
            const dropdown = document.getElementById("filterDropdown");
            const arrow = document.getElementById("filterArrow");
            dropdown.classList.toggle("show");
            arrow.classList.toggle("rotate-180");
        });

        // Close dropdown when clicking outside
        document.addEventListener("click", (e) => {
            if (
                !e.target.closest("#filterBtn") &&
                !e.target.closest("#filterDropdown")
            ) {
                document.getElementById("filterDropdown").classList.remove("show");
                document.getElementById("filterArrow").classList.remove("rotate-180");
            }
        });

        // Pagination
        document
            .getElementById("prevBtn")
            .addEventListener("click", () => this.goToPrevPage());
        document
            .getElementById("nextBtn")
            .addEventListener("click", () => this.goToNextPage());

        // Documentation button
        document.getElementById("docBtn").addEventListener("click", () => {
            window.location.href = "doc.html";
        });
    }

    populateFilters() {
        const types = [...new Set(this.apis.map((api) => api.requestType))];
        const filterOptions = document.getElementById("filterOptions");

        types.forEach((type) => {
            const option = document.createElement("a");
            option.href = "#";
            option.className =
                "filter-option block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100";
            option.dataset.type = type.toLowerCase();
            option.textContent = type;
            option.addEventListener("click", (e) => {
                e.preventDefault();
                this.setFilter(type.toLowerCase());
            });
            filterOptions.appendChild(option);
        });
    }

    setFilter(type) {
        this.currentFilter = type;
        const filterText = document.getElementById("filterText");
        filterText.textContent = type === "all" ? "All Types" : type.toUpperCase();

        document.getElementById("filterDropdown").classList.remove("show");
        document.getElementById("filterArrow").classList.remove("rotate-180");

        this.filterAPIs();
    }

    filterAPIs() {
        this.filteredApis = this.apis.filter((api) => {
            const matchesSearch =
                !this.searchQuery ||
                api.apiName.toLowerCase().includes(this.searchQuery) ||
                api.apiURL.toLowerCase().includes(this.searchQuery) ||
                api.requestType.toLowerCase().includes(this.searchQuery);

            const matchesFilter =
                this.currentFilter === "all" ||
                api.requestType.toLowerCase() === this.currentFilter;

            return matchesSearch && matchesFilter;
        });

        this.currentPage = 1;
        this.renderAPIs();
    }

    goToPrevPage() {
        if (this.currentPage > 1) {
            this.currentPage--;
            this.renderAPIs();
        }
    }

    goToNextPage() {
        const totalPages = Math.ceil(this.filteredApis.length / this.itemsPerPage);
        if (this.currentPage < totalPages) {
            this.currentPage++;
            this.renderAPIs();
        }
    }

    renderAPIs() {
        const container = document.getElementById("apiContainer");
        const noResults = document.getElementById("noResults");

        if (this.filteredApis.length === 0) {
            container.classList.add("hidden");
            noResults.classList.remove("hidden");
            this.updatePaginationInfo();
            return;
        }

        noResults.classList.add("hidden");
        container.classList.remove("hidden");

        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;
        const pageAPIs = this.filteredApis.slice(startIndex, endIndex);

        container.innerHTML = "";
        pageAPIs.forEach((api, index) => {
            const card = this.createAPICard(api, startIndex + index);
            container.appendChild(card);
        });

        this.updatePaginationInfo();
    }

    createAPICard(api, index) {
        const card = document.createElement("div");
        card.className = "bg-white rounded-lg border-custom card-hover card-enter";
        card.style.animationDelay = `${index * 0.1}s`;

        const successRate = Math.round((parseInt(api.passedTests) / api.totalTests) * 100);
        const statusColor = successRate === 100 ? "green" : successRate >= 80 ? "yellow" : "red";
        const statusBg = {
            green: "bg-green-100 text-green-800",
            yellow: "bg-yellow-100 text-yellow-800",
            red: "bg-red-100 text-red-800",
        }[statusColor];

        const methodColor = {
            GET: "bg-blue-100 text-blue-800",
            POST: "bg-green-100 text-green-800",
            PUT: "bg-orange-100 text-orange-800",
            DELETE: "bg-red-100 text-red-800",
            PATCH: "bg-purple-100 text-purple-800",
        }[api.requestType] || "bg-gray-100 text-gray-800";

        let bugsSection = "";
        if (api.bugs && api.bugs.length > 0) {
            bugsSection = `
                <div class="border-t pt-4">
                    <button 
                        onclick="toggleBugs(${index})"
                        class="flex items-center justify-between w-full text-left text-sm font-medium text-gray-700 hover:text-gray-900"
                    >
                        <span class="flex items-center">
                            <span class="material-icons text-red-500 mr-2">bug_report</span>
                            View Bug Details (${api.bugs.length})
                        </span>
                        <span class="material-icons expand-animation" id="arrow-${index}">keyboard_arrow_down</span>
                    </button>
                    <div id="bugs-${index}" class="hidden mt-3 space-y-3">
                        ${api.bugs
                            .map(
                                (bug) => `
                                    <div class="bg-red-50 border border-red-200 rounded-md p-3">
                                        <div class="text-sm font-medium text-red-800 mb-2">${bug.bugDesc}</div>
                                        <div class="space-y-1">
                                            <div class="text-xs text-red-700"><strong>Expected:</strong> ${bug.expected}</div>
                                            <div class="text-xs text-red-700"><strong>Actual:</strong> ${bug.actual}</div>
                                        </div>
                                    </div>
                                `
                            )
                            .join("")}
                    </div>
                </div>
            `;
        } else if (successRate === 100) {
            // All tests passed, show a beautiful card
            bugsSection = `
                <div class="border-t pt-4 flex flex-col items-center justify-center">
                    <div class="flex flex-col items-center justify-center py-4">
                        <span class="material-icons text-green-500 text-6xl mb-2" style="font-size: 3.5rem;">check_circle</span>
                        <div class="text-lg font-semibold text-green-700">All Tests Are Passed</div>
                    </div>
                </div>
            `;
        }

        card.innerHTML = `
            <div class="p-6">
                <div class="flex items-start justify-between mb-4">
                    <div>
                        <h3 class="text-lg font-semibold text-gray-900 mb-2">${api.apiName}</h3>
                        <div class="flex items-center space-x-3 mb-3">
                            <span class="px-2 py-1 text-xs font-medium rounded-full ${methodColor}">${api.requestType}</span>
                            <span class="px-2 py-1 text-xs font-medium rounded-full ${statusBg}">${successRate}% Pass Rate</span>
                        </div>
                    </div>
                    <div class="text-right">
                        <div class="text-sm text-gray-500">Tests: ${api.totalTests}</div>
                        <div class="text-sm"><span class="text-green-600">${api.passedTests} passed</span> • <span class="text-red-600">${api.failedTests} failed</span></div>
                    </div>
                </div>

                <div class="flex items-center justify-between mb-4">
                    <div class="flex-1 mr-3">
                        <div class="text-sm text-gray-500 mb-1">Endpoint</div>
                        <code class="text-sm text-gray-900 bg-gray-100 px-2 py-1 rounded break-all">${api.apiURL}</code>
                    </div>
                    <button 
                        onclick="copyURL('${api.apiURL}', ${index})"
                        class="flex-shrink-0 p-2 text-gray-400 hover:text-gray-600 transition-colors btn-hover"
                        id="copyBtn-${index}"
                        title="Copy URL"
                    >
                        <span class="material-icons text-sm">content_copy</span>
                    </button>
                </div>

                ${bugsSection}
            </div>
        `;

        return card;
    }

    updatePaginationInfo() {
        const totalPages = Math.ceil(this.filteredApis.length / this.itemsPerPage);
        document.getElementById("currentPage").textContent = this.currentPage;
        document.getElementById("totalPages").textContent = totalPages || 1;

        document.getElementById("prevBtn").disabled = this.currentPage === 1;
        document.getElementById("nextBtn").disabled =
            this.currentPage >= totalPages;

        document
            .getElementById("prevBtn")
            .classList.toggle("opacity-50", this.currentPage === 1);
        document
            .getElementById("nextBtn")
            .classList.toggle("opacity-50", this.currentPage >= totalPages);
    }
}

// Global functions
function copyURL(url, index) {
    navigator.clipboard
        .writeText(url)
        .then(() => {
            const btn = document.getElementById(`copyBtn-${index}`);
            const icon = btn.querySelector(".material-icons");
            const originalText = icon.textContent;

            // Add success state
            icon.textContent = "check";
            icon.style.color = "#10b981";
            icon.classList.add("success-tick");
            btn.title = "Copied!";

            setTimeout(() => {
                icon.textContent = originalText;
                icon.style.color = "";
                icon.classList.remove("success-tick");
                btn.title = "Copy URL";
            }, 2000);
        })
        .catch((err) => {
            console.error("Failed to copy URL:", err);
            // Fallback for browsers that don't support clipboard API
            const textArea = document.createElement("textarea");
            textArea.value = url;
            document.body.appendChild(textArea);
            textArea.select();
            document.execCommand("copy");
            document.body.removeChild(textArea);

            const btn = document.getElementById(`copyBtn-${index}`);
            const icon = btn.querySelector(".material-icons");
            icon.textContent = "check";
            icon.style.color = "#10b981";
            setTimeout(() => {
                icon.textContent = "content_copy";
                icon.style.color = "";
            }, 2000);
        });
}

function toggleBugs(index) {
    const bugsDiv = document.getElementById(`bugs-${index}`);
    const arrow = document.getElementById(`arrow-${index}`);

    if (bugsDiv.classList.contains("hidden")) {
        bugsDiv.classList.remove("hidden");
        bugsDiv.classList.add("fade-in");
        arrow.style.transform = "rotate(180deg)";
    } else {
        bugsDiv.classList.add("hidden");
        bugsDiv.classList.remove("fade-in");
        arrow.style.transform = "rotate(0deg)";
    }
}

// Initialize dashboard
document.addEventListener("DOMContentLoaded", () => {
    new APIDashboard();
});

// Add filter option click handlers
document.addEventListener("click", (e) => {
    if (e.target.classList.contains("filter-option")) {
        e.preventDefault();
        // This will be handled by the event listeners set up in populateFilters()
    }
});
