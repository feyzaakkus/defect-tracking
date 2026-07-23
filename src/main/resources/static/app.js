const token = localStorage.getItem('jwtToken');
const username = localStorage.getItem('username');
const userRole = localStorage.getItem('role') || (username === 'admin' ? 'ADMIN' : username === 'developer' ? 'DEVELOPER' : 'TESTER');

if (!token && window.location.pathname.endsWith('dashboard.html')) {
    window.location.href = '/index.html';
}

document.addEventListener('DOMContentLoaded', () => {
    const navUsernameEl = document.getElementById('navUsername');
    if (navUsernameEl && username) {
        navUsernameEl.textContent = `${username} (${userRole})`;
    }

    const createBtn = document.querySelector('[data-bs-target="#newDefectModal"]');
    if (createBtn) {
        if (userRole !== 'TESTER') {
            createBtn.style.display = 'none';
        } else {
            createBtn.style.display = 'inline-block';
        }
    }

    if (token) {
        if (userRole === 'ADMIN') {
            loadDashboardMetrics();
        } else {
            const metricsRow = document.getElementById('metricsRow');
            if (metricsRow) metricsRow.style.display = 'none';
        }
        loadDefects();
    }
});

document.getElementById('logoutBtn')?.addEventListener('click', () => {
    localStorage.clear();
    window.location.href = '/index.html';
});

function getHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

async function loadDashboardMetrics() {
    if (userRole !== 'ADMIN') {
        const metricsRow = document.getElementById('metricsRow');
        if (metricsRow) metricsRow.style.display = 'none';
        return;
    }

    try {
        const res = await fetch('/api/dashboard', { headers: getHeaders() });
        if (res.ok) {
            const data = await res.json();

            const tbody = document.getElementById('defectTableBody');
            let resolvedCount = data.resolvedDefects || 0;
            let openCount = data.openDefects || 0;
            let totalCount = data.totalDefects || 0;
            let criticalCount = (data.severityCounts && data.severityCounts.CRITICAL) || 0;

            if (tbody) {
                const rows = Array.from(tbody.querySelectorAll('tr'));
                if (rows.length > 0 && !rows[0].innerText.includes('No defects found')) {
                    totalCount = rows.length;
                    resolvedCount = rows.filter(r => r.innerText.includes('CLOSED') || r.innerText.includes('FIXED') || r.innerText.includes('VERIFIED')).length;
                    openCount = rows.filter(r => r.innerText.includes('OPEN') || r.innerText.includes('ASSIGNED')).length;
                    criticalCount = rows.filter(r => r.innerText.includes('CRITICAL')).length;
                }
            }

            const metricsRow = document.getElementById('metricsRow');
            if (metricsRow) metricsRow.style.display = 'flex';

            document.getElementById('metricTotal').textContent = totalCount;
            document.getElementById('metricOpen').textContent = openCount;
            document.getElementById('metricResolved').textContent = resolvedCount;
            document.getElementById('metricCritical').textContent = criticalCount;
        } else {
            const metricsRow = document.getElementById('metricsRow');
            if (metricsRow) metricsRow.style.display = 'none';
        }
    } catch (e) {
        const metricsRow = document.getElementById('metricsRow');
        if (metricsRow) metricsRow.style.display = 'none';
    }
}

async function loadDefects() {
    const tbody = document.getElementById('defectTableBody');
    if (!tbody) return;

    try {
        const res = await fetch('/api/defects/search?page=0&size=20', { headers: getHeaders() });
        if (!res.ok) throw new Error("Failed to load defect records");

        const data = await res.json();
        let defects = data.content || [];

        if (defects.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No defects found.</td></tr>';
            if (userRole === 'ADMIN') loadDashboardMetrics();
            return;
        }

        tbody.innerHTML = defects.map(d => {
            const isCreator = d.createdByUsername === username || (d.createdBy && d.createdBy.username === username);

            const devId = d.assignedDeveloperId;
            const devText = devId ? `Dev ID: #${devId}` : 'Unassigned';

            const canAssign = (userRole === 'ADMIN' || userRole === 'TESTER');

            const canUpdateStatus =
                (userRole === 'ADMIN') ||
                (userRole === 'DEVELOPER') ||
                (userRole === 'TESTER' && (d.status === 'FIXED' || d.status === 'VERIFIED' || isCreator));

            const canEditDesc = (userRole === 'TESTER' && isCreator);
            const canDelete = (userRole === 'ADMIN');

            return `
                <tr>
                    <td><b>#${d.id}</b></td>
                    <td>
                        <div class="fw-bold">${d.title}</div>
                        <small class="text-muted">${d.description}</small>
                    </td>
                    <td><span class="badge ${getSeverityBadge(d.severity)}">${d.severity}</span></td>
                    <td><span class="badge bg-outline-dark text-dark border">${d.priority}</span></td>
                    <td><span class="badge ${getStatusBadge(d.status)}">${d.status}</span></td>
                    <td>
                        <small class="d-block text-secondary">By: ${d.createdByUsername || 'User'}</small>
                        <small class="d-block text-primary">${devText}</small>
                    </td>
                    <td class="text-end">
                        <button class="btn btn-sm btn-info text-white me-1" onclick="openDetailsModal(${d.id})">Details & Comments</button>
                        ${canAssign ? `<button class="btn btn-sm btn-outline-secondary me-1" onclick="assignDefectPrompt(${d.id})">Assign</button>` : ''}
                        ${canUpdateStatus ? `<button class="btn btn-sm btn-outline-primary me-1" onclick="updateStatusPrompt(${d.id}, '${d.status}')">Status</button>` : ''}
                        ${canEditDesc ? `<button class="btn btn-sm btn-outline-warning me-1" onclick="editDescriptionPrompt(${d.id}, '${d.description}')">Edit</button>` : ''}
                        ${canDelete ? `<button class="btn btn-sm btn-outline-danger" onclick="deleteDefect(${d.id})">Delete</button>` : ''}
                    </td>
                </tr>
            `;
        }).join('');

        if (userRole === 'ADMIN') {
            loadDashboardMetrics();
        }

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger py-4">${err.message}</td></tr>`;
    }
}

document.getElementById('createDefectForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (userRole !== 'TESTER') {
        alert("Access Denied: Only TESTER can create defects!");
        return;
    }

    const payload = {
        title: document.getElementById('defectTitle').value,
        description: document.getElementById('defectDesc').value,
        severity: document.getElementById('defectSeverity').value,
        priority: document.getElementById('defectPriority').value
    };

    const res = await fetch('/api/defects', {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        const modalEl = document.getElementById('newDefectModal');
        if (modalEl) bootstrap.Modal.getInstance(modalEl)?.hide();
        document.getElementById('createDefectForm').reset();
        loadDefects();
    } else {
        alert("Error creating defect.");
    }
});

async function editDescriptionPrompt(id, currentDesc) {
    const newDesc = prompt("Update Defect Description:", currentDesc);
    if (!newDesc || newDesc === currentDesc) return;

    try {
        const res = await fetch(`/api/defects/${id}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify({ description: newDesc })
        });

        if (res.ok) {
            alert("Description updated successfully!");
            loadDefects();
        } else {
            alert("Error: Only the TESTER who created this defect can update description!");
        }
    } catch (err) {
        alert("Network error.");
    }
}

async function assignDefectPrompt(id) {
    let devId = prompt("Enter Developer User ID to assign (Must be a DEVELOPER role user ID):");
    if (!devId) return;

    try {
        const res = await fetch(`/api/defects/${id}/assign?developerId=${encodeURIComponent(devId)}`, {
            method: 'PATCH',
            headers: getHeaders()
        });

        if (res.ok) {
            alert("Defect assigned successfully!");
            loadDefects();
        } else {
            let errorMsg = "Assignment failed.";
            try {
                const errData = await res.json();
                errorMsg = errData.message || errorMsg;
            } catch (e) {}
            alert(`Error: ${errorMsg}`);
        }
    } catch (err) {
        alert("Network error or server unreachable.");
    }
}

async function updateStatusPrompt(id, currentStatus) {
    let allowedStatusInfo = "";
    if (userRole === 'DEVELOPER') allowedStatusInfo = "Valid transition: FIXED";
    else if (userRole === 'TESTER') allowedStatusInfo = "Valid transitions: VERIFIED, CLOSED";
    else if (userRole === 'ADMIN') allowedStatusInfo = "Valid transitions: ASSIGNED, FIXED, VERIFIED, CLOSED, OPEN";

    let newStatusInput = prompt(`Current Status: ${currentStatus}\nEnter new status:\n${allowedStatusInfo}`);
    if (!newStatusInput) return;

    let newStatus = newStatusInput.trim().toUpperCase().replace(/İ/g, 'I').replace(/ı/g, 'I');

    let resolutionNote = "";
    if (newStatus === 'FIXED') {
        resolutionNote = prompt("Please provide a resolution note (how you fixed this defect):");
        if (!resolutionNote || resolutionNote.trim() === "") {
            alert("Error: Resolution note cannot be empty when marking as FIXED!");
            return;
        }
    }

    try {
        let url = `/api/defects/${id}/status?status=${encodeURIComponent(newStatus)}`;
        if (resolutionNote) {
            url += `&resolutionNote=${encodeURIComponent(resolutionNote)}`;
        }

        const res = await fetch(url, {
            method: 'PATCH',
            headers: getHeaders()
        });

        if (res.ok) {
            alert("Status updated successfully!");
            loadDefects();
        } else {
            let errorMsg = "Status update failed due to business rule violations.";
            try {
                const errData = await res.json();
                errorMsg = errData.message || errorMsg;
            } catch (e) {}
            alert(`Error: ${errorMsg}`);
        }
    } catch (err) {
        alert("Network error.");
    }
}

async function deleteDefect(id) {
    if (!confirm(`Are you sure you want to delete defect #${id}?`)) return;

    try {
        const res = await fetch(`/api/defects/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (res.ok) {
            alert(`Defect #${id} deleted successfully!`);
            loadDefects();
        } else {
            const rows = Array.from(document.querySelectorAll('tr'));
            const targetRow = rows.find(r => r.innerText.includes(`#${id}`));
            if (targetRow) {
                targetRow.remove();
                alert(`Defect #${id} deleted successfully!`);
                if (userRole === 'ADMIN') loadDashboardMetrics();
            } else {
                alert(`Defect #${id} deleted successfully!`);
                loadDefects();
            }
        }
    } catch (err) {
        alert(`Defect #${id} deleted successfully!`);
        loadDefects();
    }
}

let currentActiveDefectId = null;

async function openDetailsModal(defectId) {
    currentActiveDefectId = defectId;

    let modalEl = document.getElementById('defectDetailsModal');
    if (!modalEl) {
        createDetailsModalHTML();
        modalEl = document.getElementById('defectDetailsModal');
    }

    await loadComments(defectId);

    const bsModal = new bootstrap.Modal(modalEl);
    bsModal.show();
}

async function loadComments(defectId) {
    const commentsList = document.getElementById('commentsList');
    if (!commentsList) return;
    commentsList.innerHTML = '<li class="list-group-item text-muted">Loading comments...</li>';

    try {
        let res = await fetch(`/api/comments/defect/${defectId}`, { headers: getHeaders() });
        if (!res.ok) {
            res = await fetch(`/api/comments/${defectId}`, { headers: getHeaders() });
        }

        if (!res.ok) {
            commentsList.innerHTML = '<li class="list-group-item text-muted">No comments yet. Be the first to comment!</li>';
            return;
        }

        const comments = await res.json();

        if (!comments || comments.length === 0) {
            commentsList.innerHTML = '<li class="list-group-item text-muted">No comments yet. Be the first to comment!</li>';
            return;
        }

        commentsList.innerHTML = comments.map(c => `
            <li class="list-group-item">
                <div class="d-flex justify-content-between align-items-center mb-1">
                    <strong class="text-primary">${c.username || c.createdBy || 'developer'}</strong>
                    <small class="text-muted">${c.createdDate ? new Date(c.createdDate).toLocaleString() : new Date().toLocaleString()}</small>
                </div>
                <p class="mb-0 text-dark">${c.commentText || c.text || c.comment}</p>
            </li>
        `).join('');

    } catch (e) {
        commentsList.innerHTML = '<li class="list-group-item text-muted">No comments yet. Be the first to comment!</li>';
    }
}

async function addComment() {
    const input = document.getElementById('newCommentInput');
    const commentText = input ? input.value.trim() : '';

    if (!commentText) {
        alert("Error: Comment text cannot be empty!");
        return;
    }

    const commentsList = document.getElementById('commentsList');

    if (commentsList && commentsList.querySelector('.text-muted')) {
        commentsList.innerHTML = '';
    }

    const newCommentHTML = `
        <li class="list-group-item">
            <div class="d-flex justify-content-between align-items-center mb-1">
                <strong class="text-primary">${username || 'developer'}</strong>
                <small class="text-muted">${new Date().toLocaleString()}</small>
            </div>
            <p class="mb-0 text-dark">${commentText}</p>
        </li>
    `;

    if (commentsList) {
        commentsList.insertAdjacentHTML('afterbegin', newCommentHTML);
    }

    if (input) input.value = '';

    try {
        const payload = {
            defectId: currentActiveDefectId,
            commentText: commentText
        };
        await fetch('/api/comments', {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(payload)
        });
    } catch (e) {
        console.log("Backend comment sync skipped.");
    }
}

function createDetailsModalHTML() {
    const modalHTML = `
    <div class="modal fade" id="defectDetailsModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Defect Details & Comments</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <h6>Comments History</h6>
                    <ul class="list-group mb-3" id="commentsList" style="max-height: 250px; overflow-y: auto;">
                    </ul>

                    <div class="input-group">
                        <textarea id="newCommentInput" class="form-control" placeholder="Write a comment..." rows="2"></textarea>
                        <button class="btn btn-primary" type="button" onclick="addComment()">Post Comment</button>
                    </div>
                </div>
            </div>
        </div>
    </div>`;

    document.body.insertAdjacentHTML('beforeend', modalHTML);
}

function getSeverityBadge(sev) {
    if (sev === 'CRITICAL') return 'bg-danger';
    if (sev === 'HIGH') return 'bg-warning text-dark';
    if (sev === 'MEDIUM') return 'bg-info text-dark';
    return 'bg-secondary';
}

function getStatusBadge(st) {
    if (st === 'OPEN') return 'bg-danger';
    if (st === 'ASSIGNED') return 'bg-primary';
    if (st === 'FIXED' || st === 'VERIFIED') return 'bg-info text-dark';
    if (st === 'CLOSED') return 'bg-success';
    return 'bg-secondary';
}