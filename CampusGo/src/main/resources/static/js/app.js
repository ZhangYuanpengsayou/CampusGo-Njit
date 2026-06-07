const { createApp } = Vue;

function createDefaultRegisterForm() {
    return {
        username: "",
        password: "",
        confirmPassword: "",
        role: "STUDENT",
        realName: "",
        phone: "",
        email: "",
        college: "",
        applicationCode: "",
        studentNo: "",
        major: "",
        className: "",
        dormBuilding: "",
        dormRoom: "",
        teacherNo: "",
        title: "",
        office: "",
        adminNo: "",
        department: ""
    };
}

createApp({
    data() {
        return {
            roles: { STUDENT: "STUDENT", TEACHER: "TEACHER", ADMIN: "ADMIN" },
            view: "home",
            authMode: "login",
            loading: false,
            currentUser: null,
            toast: { message: "", type: "success" },
            colleges: [],
            manageColleges: [],
            majors: [],
            classes: [],
            dormBuildings: [],
            dormRooms: [],
            profileMajors: [],
            profileClasses: [],
            profileDormRooms: [],
            manageMajors: [],
            manageClasses: [],
            manageDormBuildings: [],
            manageDormRooms: [],
            carousels: [],
            manageCarousels: [],
            announcements: { total: 0, list: [] },
            leaves: { total: 0, list: [] },
            auditLeaves: { total: 0, list: [] },
            repairs: { total: 0, list: [] },
            auditRepairs: { total: 0, list: [] },
            selectedAnnouncement: {},
            selectedRepair: null,
            announcementModal: null,
            rateModal: null,
            repairAuditModal: null,
            confirmationModal: null,
            confirmation: { title: "确认操作", message: "", confirmText: "确认", danger: false, resolve: null },
            studentTab: "leave",
            adminTab: "announcements",
            loginForm: { username: "", password: "" },
            registerForm: createDefaultRegisterForm(),
            profileForm: {},
            majorForm: { id: null, collegeName: "", name: "", status: 1 },
            classForm: { id: null, collegeName: "", majorName: "", name: "", status: 1 },
            dormBuildingForm: { id: null, name: "", status: 1 },
            dormRoomForm: { id: null, buildingName: "", roomNo: "", status: 1 },
            carouselForm: { id: null, title: "", subtitle: "", imageUrl: "", sortOrder: 1, status: 1 },
            announcementForm: { id: null, title: "", content: "" },
            collegeForm: { id: null, name: "", description: "", status: 1 },
            leaveForm: { reason: "", startTime: "", endTime: "" },
            repairForm: { reason: "", photoUrl: "" },
            repairAuditForm: { repairId: null, repairmanPhone: "", auditOpinion: "已安排维修" },
            rateForm: { score: 5, comment: "" },
            announcementQuery: { page: 1, pageSize: 6, keyword: "" },
            leaveQuery: { page: 1, pageSize: 10, status: "" },
            repairQuery: { page: 1, pageSize: 10, status: "" },
            auditLeaveQuery: { page: 1, pageSize: 10, status: "PENDING", studentName: "" },
            auditRepairQuery: { page: 1, pageSize: 10, status: "PENDING", studentName: "", dormBuilding: "" },
            leaveStatuses: ["PENDING", "APPROVED", "REJECTED", "CANCELED", "RETURNED"],
            repairStatuses: ["PENDING", "APPROVED", "REJECTED", "CANCELED", "REPAIRING", "COMPLETED", "RATED"]
        };
    },
    async mounted() {
        this.announcementModal = new bootstrap.Modal(document.getElementById("announcementModal"));
        this.rateModal = new bootstrap.Modal(document.getElementById("rateModal"));
        this.repairAuditModal = new bootstrap.Modal(document.getElementById("repairAuditModal"));
        const confirmationElement = document.getElementById("confirmationModal");
        this.confirmationModal = new bootstrap.Modal(confirmationElement);
        confirmationElement.addEventListener("hidden.bs.modal", () => {
            if (this.confirmation.resolve) {
                this.confirmation.resolve(false);
                this.confirmation.resolve = null;
            }
        });
        await Promise.all([this.loadCarousels(), this.loadAnnouncements(), this.loadColleges(), this.loadDormBuildings(), this.loadMe()]);
        this.refreshIcons();
    },
    updated() {
        this.refreshIcons();
    },
    methods: {
        async apiCall(action, successMessage) {
            this.loading = true;
            try {
                const data = await action();
                if (successMessage) {
                    this.showToast(successMessage);
                }
                return data;
            } catch (error) {
                this.showToast(error.message || "操作失败", "error");
                if (error.code === 401) {
                    this.currentUser = null;
                    this.showAuth("login");
                }
                return null;
            } finally {
                this.loading = false;
            }
        },
        queryString(params) {
            const search = new URLSearchParams();
            Object.entries(params).forEach(([key, value]) => {
                if (value !== undefined && value !== null && value !== "") {
                    search.append(key, value);
                }
            });
            const text = search.toString();
            return text ? `?${text}` : "";
        },
        async loadMe() {
            try {
                this.currentUser = await CampusGoApi.get("/api/users/me");
                this.profileForm = { ...this.currentUser };
            } catch (error) {
                this.currentUser = null;
            }
        },
        async loadColleges() {
            this.colleges = await this.apiCall(() => CampusGoApi.get("/api/colleges")) || [];
        },
        async loadManageColleges() {
            this.manageColleges = await this.apiCall(() => CampusGoApi.get("/api/colleges/manage")) || [];
        },
        async loadMajors() {
            if (!this.registerForm.college) {
                this.majors = [];
                return;
            }
            const college = encodeURIComponent(this.registerForm.college);
            this.majors = await this.apiCall(() => CampusGoApi.get(`/api/majors?college=${college}`)) || [];
        },
        async loadClasses() {
            if (!this.registerForm.college || !this.registerForm.major) {
                this.classes = [];
                return;
            }
            const college = encodeURIComponent(this.registerForm.college);
            const major = encodeURIComponent(this.registerForm.major);
            this.classes = await this.apiCall(() => CampusGoApi.get(`/api/classes?college=${college}&major=${major}`)) || [];
        },
        async loadDormBuildings() {
            this.dormBuildings = await this.apiCall(() => CampusGoApi.get("/api/dorm-buildings")) || [];
        },
        async loadDormRooms() {
            if (!this.registerForm.dormBuilding) {
                this.dormRooms = [];
                return;
            }
            const building = encodeURIComponent(this.registerForm.dormBuilding);
            this.dormRooms = await this.apiCall(() => CampusGoApi.get(`/api/dorm-rooms?building=${building}`)) || [];
        },
        async loadProfileOptions() {
            if (!this.profileForm || !this.profileForm.college) {
                this.profileMajors = [];
                this.profileClasses = [];
            } else {
                const college = encodeURIComponent(this.profileForm.college);
                this.profileMajors = await this.apiCall(() => CampusGoApi.get(`/api/majors?college=${college}`)) || [];
                if (this.profileForm.major) {
                    const major = encodeURIComponent(this.profileForm.major);
                    this.profileClasses = await this.apiCall(() => CampusGoApi.get(`/api/classes?college=${college}&major=${major}`)) || [];
                }
            }
            if (!this.profileForm || !this.profileForm.dormBuilding) {
                this.profileDormRooms = [];
            } else {
                const building = encodeURIComponent(this.profileForm.dormBuilding);
                this.profileDormRooms = await this.apiCall(() => CampusGoApi.get(`/api/dorm-rooms?building=${building}`)) || [];
            }
        },
        async loadManageMajors() {
            this.manageMajors = await this.apiCall(() => CampusGoApi.get("/api/majors/manage")) || [];
        },
        async loadManageClasses() {
            this.manageClasses = await this.apiCall(() => CampusGoApi.get("/api/classes/manage")) || [];
        },
        async loadManageDormBuildings() {
            this.manageDormBuildings = await this.apiCall(() => CampusGoApi.get("/api/dorm-buildings/manage")) || [];
        },
        async loadManageDormRooms() {
            this.manageDormRooms = await this.apiCall(() => CampusGoApi.get("/api/dorm-rooms/manage")) || [];
        },
        async loadCarousels() {
            this.carousels = await this.apiCall(() => CampusGoApi.get("/api/carousels")) || [];
        },
        async loadManageCarousels() {
            this.manageCarousels = await this.apiCall(() => CampusGoApi.get("/api/carousels/manage")) || [];
        },
        async loadAnnouncements() {
            const query = this.queryString(this.announcementQuery);
            this.announcements = await this.apiCall(() => CampusGoApi.get(`/api/announcements${query}`)) || { total: 0, list: [] };
        },
        async login() {
            const user = await this.apiCall(() => CampusGoApi.post("/api/auth/login", this.loginForm), "登录成功");
            if (!user) {
                return;
            }
            this.currentUser = user;
            await this.loadMe();
            this.routeByRole();
        },
        async register() {
            const payload = { ...this.registerForm };
            if (payload.role === this.roles.ADMIN) {
                payload.college = null;
            }
            const ok = await this.apiCall(() => CampusGoApi.post("/api/auth/register", payload), "注册成功，请登录");
            if (ok !== null) {
                this.registerForm = createDefaultRegisterForm();
                this.showAuth("login");
            }
        },
        async logout() {
            this.closeMainNav();
            await this.apiCall(() => CampusGoApi.post("/api/auth/logout"), "已退出");
            this.currentUser = null;
            this.goHome();
        },
        async updateProfile() {
            const user = await this.apiCall(() => CampusGoApi.put("/api/users/me", this.profileForm), "资料已保存");
            if (user) {
                this.currentUser = {
                    id: user.id,
                    username: user.username,
                    role: user.role,
                    realName: user.realName,
                    college: user.college
                };
                this.profileForm = { ...user };
            }
        },
        showAuth(mode) {
            this.closeMainNav();
            this.authMode = mode;
            this.view = "auth";
        },
        goHome() {
            this.closeMainNav();
            this.view = "home";
            this.loadCarousels();
            this.loadAnnouncements();
        },
        closeMainNav() {
            const nav = document.getElementById("mainNav");
            if (nav && nav.classList.contains("show") && window.bootstrap) {
                bootstrap.Collapse.getOrCreateInstance(nav, { toggle: false }).hide();
            }
        },
        routeByRole() {
            if (!this.currentUser) {
                this.goHome();
            } else if (this.currentUser.role === this.roles.STUDENT) {
                this.showStudent();
            } else if (this.currentUser.role === this.roles.TEACHER) {
                this.showTeacher();
            } else {
                this.showAdmin();
            }
        },
        async showStudent() {
            this.closeMainNav();
            this.view = "student";
            this.studentTab = "leave";
            await this.loadMyLeaves();
            await this.loadMyRepairs();
        },
        async showTeacher() {
            this.closeMainNav();
            this.view = "teacher";
            await this.loadAuditLeaves();
        },
        async showAdmin() {
            this.closeMainNav();
            this.view = "admin";
            this.adminTab = "announcements";
            await this.loadAnnouncements();
        },
        async showProfile() {
            this.closeMainNav();
            this.view = "profile";
            await this.loadMe();
            await this.loadProfileOptions();
        },
        async onRegisterCollegeChange() {
            this.registerForm.major = "";
            this.registerForm.className = "";
            this.majors = [];
            this.classes = [];
            if (this.registerForm.college) {
                await this.loadMajors();
            }
        },
        async onRegisterMajorChange() {
            this.registerForm.className = "";
            this.classes = [];
            if (this.registerForm.major) {
                await this.loadClasses();
            }
        },
        async onRegisterDormBuildingChange() {
            this.registerForm.dormRoom = "";
            this.dormRooms = [];
            if (this.registerForm.dormBuilding) {
                await this.loadDormRooms();
            }
        },
        async onProfileCollegeChange() {
            this.profileForm.major = "";
            this.profileForm.className = "";
            this.profileMajors = [];
            this.profileClasses = [];
            if (this.profileForm.college) {
                const college = encodeURIComponent(this.profileForm.college);
                this.profileMajors = await this.apiCall(() => CampusGoApi.get(`/api/majors?college=${college}`)) || [];
            }
        },
        async onProfileMajorChange() {
            this.profileForm.className = "";
            this.profileClasses = [];
            if (this.profileForm.college && this.profileForm.major) {
                const college = encodeURIComponent(this.profileForm.college);
                const major = encodeURIComponent(this.profileForm.major);
                this.profileClasses = await this.apiCall(() => CampusGoApi.get(`/api/classes?college=${college}&major=${major}`)) || [];
            }
        },
        async onProfileDormBuildingChange() {
            this.profileForm.dormRoom = "";
            this.profileDormRooms = [];
            if (this.profileForm.dormBuilding) {
                const building = encodeURIComponent(this.profileForm.dormBuilding);
                this.profileDormRooms = await this.apiCall(() => CampusGoApi.get(`/api/dorm-rooms?building=${building}`)) || [];
            }
        },
        async createLeave() {
            const payload = {
                reason: this.leaveForm.reason,
                startTime: this.toLocalDateTime(this.leaveForm.startTime),
                endTime: this.toLocalDateTime(this.leaveForm.endTime)
            };
            const ok = await this.apiCall(() => CampusGoApi.post("/api/leaves", payload), "请假申请已提交");
            if (ok) {
                this.leaveForm = { reason: "", startTime: "", endTime: "" };
                this.loadMyLeaves();
            }
        },
        async loadMyLeaves() {
            const query = this.queryString(this.leaveQuery);
            this.leaves = await this.apiCall(() => CampusGoApi.get(`/api/leaves/my${query}`)) || { total: 0, list: [] };
        },
        async cancelLeave(id) {
            if (!await this.confirmAction("确认撤销这条请假申请？", { confirmText: "撤销申请" })) {
                return;
            }
            await this.apiCall(() => CampusGoApi.put(`/api/leaves/${id}/cancel`, {}), "请假申请已撤销");
            this.loadMyLeaves();
        },
        async returnLeave(id) {
            if (!await this.confirmAction("确认已经返校并完成销假？", { confirmText: "确认销假" })) {
                return;
            }
            await this.apiCall(() => CampusGoApi.put(`/api/leaves/${id}/return`, {}), "销假成功");
            this.loadMyLeaves();
        },
        async loadAuditLeaves() {
            const query = this.queryString(this.auditLeaveQuery);
            this.auditLeaves = await this.apiCall(() => CampusGoApi.get(`/api/leaves/audit${query}`)) || { total: 0, list: [] };
        },
        async auditLeave(id, status) {
            const approved = status === "APPROVED";
            if (!await this.confirmAction(approved ? "确认通过该请假申请？" : "确认不通过该请假申请？", {
                confirmText: approved ? "通过" : "不通过",
                danger: !approved
            })) {
                return;
            }
            const opinion = status === "APPROVED" ? "同意请假" : "不同意请假";
            await this.apiCall(() => CampusGoApi.put(`/api/leaves/${id}/audit`, { status, auditOpinion: opinion }), "审核完成");
            this.loadAuditLeaves();
        },
        async uploadRepairPhoto(event) {
            const file = event.target.files[0];
            if (!file) {
                return;
            }
            const formData = new FormData();
            formData.append("file", file);
            const result = await this.apiCall(() => CampusGoApi.upload("/api/files/repair-photo", formData), "照片上传成功");
            if (result) {
                this.repairForm.photoUrl = result.url;
            }
        },
        async createRepair() {
            const ok = await this.apiCall(() => CampusGoApi.post("/api/repairs", this.repairForm), "报修申请已提交");
            if (ok) {
                this.repairForm = { reason: "", photoUrl: "" };
                this.loadMyRepairs();
            }
        },
        async loadMyRepairs() {
            const query = this.queryString(this.repairQuery);
            this.repairs = await this.apiCall(() => CampusGoApi.get(`/api/repairs/my${query}`)) || { total: 0, list: [] };
        },
        async cancelRepair(id) {
            if (!await this.confirmAction("确认撤销这条报修申请？", { confirmText: "撤销申请" })) {
                return;
            }
            await this.apiCall(() => CampusGoApi.put(`/api/repairs/${id}/cancel`, {}), "报修申请已撤销");
            this.loadMyRepairs();
        },
        async completeRepair(id) {
            if (!await this.confirmAction("确认该报修已经维修完成？", { confirmText: "确认完成" })) {
                return;
            }
            await this.apiCall(() => CampusGoApi.put(`/api/repairs/${id}/complete`, {}), "已确认维修完成");
            this.loadMyRepairs();
        },
        async loadAuditRepairs() {
            const query = this.queryString(this.auditRepairQuery);
            this.auditRepairs = await this.apiCall(() => CampusGoApi.get(`/api/repairs/audit${query}`)) || { total: 0, list: [] };
        },
        async auditRepair(item, status) {
            if (status === "APPROVED") {
                this.repairAuditForm = {
                    repairId: item.id,
                    repairmanPhone: item.repairmanPhone || "",
                    auditOpinion: "已安排维修"
                };
                this.repairAuditModal.show();
                return;
            }
            if (!await this.confirmAction("确认不通过该报修申请？", { confirmText: "不通过", danger: true })) {
                return;
            }
            const auditOpinion = "暂不通过";
            await this.apiCall(() => CampusGoApi.put(`/api/repairs/${item.id}/audit`, { status, repairmanPhone: null, auditOpinion }), "审核完成");
            this.loadAuditRepairs();
        },
        async submitRepairApproval() {
            const payload = {
                status: "APPROVED",
                repairmanPhone: this.repairAuditForm.repairmanPhone,
                auditOpinion: this.repairAuditForm.auditOpinion
            };
            const ok = await this.apiCall(() => CampusGoApi.put(`/api/repairs/${this.repairAuditForm.repairId}/audit`, payload), "审核完成");
            if (ok) {
                this.repairAuditModal.hide();
                this.loadAuditRepairs();
            }
        },
        async updateRepairStatus(id, status) {
            if (!await this.confirmAction("确认将该报修标记为维修中？", { confirmText: "标记维修中" })) {
                return;
            }
            await this.apiCall(() => CampusGoApi.put(`/api/repairs/${id}/status`, { status }), "状态已更新");
            this.loadAuditRepairs();
        },
        openRate(item) {
            this.selectedRepair = item;
            this.rateForm = { score: 5, comment: "" };
            this.rateModal.show();
        },
        async submitRate() {
            if (!this.selectedRepair) {
                return;
            }
            const ok = await this.apiCall(() => CampusGoApi.put(`/api/repairs/${this.selectedRepair.id}/rate`, this.rateForm), "评价成功");
            if (ok) {
                this.rateModal.hide();
                this.loadMyRepairs();
            }
        },
        async uploadCarouselImage(event) {
            const file = event.target.files[0];
            if (!file) {
                return;
            }
            const formData = new FormData();
            formData.append("file", file);
            const result = await this.apiCall(() => CampusGoApi.upload("/api/files/carousel-image", formData), "图片上传成功");
            if (result) {
                this.carouselForm.imageUrl = result.url;
            }
        },
        editCarousel(item) {
            this.carouselForm = {
                id: item.id,
                title: item.title,
                subtitle: item.subtitle || "",
                imageUrl: item.imageUrl,
                sortOrder: item.sortOrder,
                status: item.status
            };
        },
        resetCarouselForm() {
            this.carouselForm = { id: null, title: "", subtitle: "", imageUrl: "", sortOrder: 1, status: 1 };
        },
        async saveCarousel() {
            const payload = {
                title: this.carouselForm.title,
                subtitle: this.carouselForm.subtitle,
                imageUrl: this.carouselForm.imageUrl,
                sortOrder: this.carouselForm.sortOrder,
                status: this.carouselForm.status
            };
            if (this.carouselForm.id) {
                await this.apiCall(() => CampusGoApi.put(`/api/carousels/${this.carouselForm.id}`, payload), "轮播图已更新");
            } else {
                await this.apiCall(() => CampusGoApi.post("/api/carousels", payload), "轮播图已新增");
            }
            this.resetCarouselForm();
            await this.loadManageCarousels();
            await this.loadCarousels();
        },
        async deleteCarousel(id) {
            if (!await this.confirmAction("确认删除这张轮播图？", { confirmText: "删除轮播图", danger: true })) {
                return;
            }
            await this.apiCall(() => CampusGoApi.delete(`/api/carousels/${id}`), "轮播图已删除");
            await this.loadManageCarousels();
            await this.loadCarousels();
        },
        editAnnouncement(item) {
            this.announcementForm = { id: item.id, title: item.title, content: item.content };
        },
        resetAnnouncementForm() {
            this.announcementForm = { id: null, title: "", content: "" };
        },
        async saveAnnouncement() {
            const payload = { title: this.announcementForm.title, content: this.announcementForm.content };
            if (this.announcementForm.id) {
                await this.apiCall(() => CampusGoApi.put(`/api/announcements/${this.announcementForm.id}`, payload), "公告已更新");
            } else {
                await this.apiCall(() => CampusGoApi.post("/api/announcements", payload), "公告已新增");
            }
            this.resetAnnouncementForm();
            this.loadAnnouncements();
        },
        async deleteAnnouncement(id) {
            if (!await this.confirmAction("确认删除这条公告？", { confirmText: "删除公告", danger: true })) {
                return;
            }
            await this.apiCall(() => CampusGoApi.delete(`/api/announcements/${id}`), "公告已删除");
            this.loadAnnouncements();
        },
        editCollege(item) {
            this.collegeForm = { id: item.id, name: item.name, description: item.description, status: item.status };
        },
        resetCollegeForm() {
            this.collegeForm = { id: null, name: "", description: "", status: 1 };
        },
        async saveCollege() {
            const payload = { name: this.collegeForm.name, description: this.collegeForm.description, status: this.collegeForm.status };
            if (this.collegeForm.id) {
                await this.apiCall(() => CampusGoApi.put(`/api/colleges/${this.collegeForm.id}`, payload), "学院已更新");
            } else {
                await this.apiCall(() => CampusGoApi.post("/api/colleges", payload), "学院已新增");
            }
            this.resetCollegeForm();
            await this.loadManageColleges();
            await this.loadColleges();
        },
        editMajor(item) {
            this.majorForm = { id: item.id, collegeName: item.collegeName, name: item.name, status: item.status };
        },
        resetMajorForm() {
            this.majorForm = { id: null, collegeName: "", name: "", status: 1 };
        },
        async saveMajor() {
            const payload = { collegeName: this.majorForm.collegeName, name: this.majorForm.name, status: this.majorForm.status };
            if (this.majorForm.id) {
                await this.apiCall(() => CampusGoApi.put(`/api/majors/${this.majorForm.id}`, payload), "专业已更新");
            } else {
                await this.apiCall(() => CampusGoApi.post("/api/majors", payload), "专业已新增");
            }
            this.resetMajorForm();
            await this.loadManageMajors();
            await this.loadManageClasses();
        },
        editClassGroup(item) {
            this.classForm = { id: item.id, collegeName: item.collegeName, majorName: item.majorName, name: item.name, status: item.status };
        },
        resetClassForm() {
            this.classForm = { id: null, collegeName: "", majorName: "", name: "", status: 1 };
        },
        async saveClassGroup() {
            const payload = { collegeName: this.classForm.collegeName, majorName: this.classForm.majorName, name: this.classForm.name, status: this.classForm.status };
            if (this.classForm.id) {
                await this.apiCall(() => CampusGoApi.put(`/api/classes/${this.classForm.id}`, payload), "班级已更新");
            } else {
                await this.apiCall(() => CampusGoApi.post("/api/classes", payload), "班级已新增");
            }
            this.resetClassForm();
            await this.loadManageClasses();
        },
        editDormBuilding(item) {
            this.dormBuildingForm = { id: item.id, name: item.name, status: item.status };
        },
        resetDormBuildingForm() {
            this.dormBuildingForm = { id: null, name: "", status: 1 };
        },
        async saveDormBuilding() {
            const payload = { name: this.dormBuildingForm.name, status: this.dormBuildingForm.status };
            if (this.dormBuildingForm.id) {
                await this.apiCall(() => CampusGoApi.put(`/api/dorm-buildings/${this.dormBuildingForm.id}`, payload), "楼栋已更新");
            } else {
                await this.apiCall(() => CampusGoApi.post("/api/dorm-buildings", payload), "楼栋已新增");
            }
            this.resetDormBuildingForm();
            await this.loadManageDormBuildings();
            await this.loadManageDormRooms();
            await this.loadDormBuildings();
        },
        editDormRoom(item) {
            this.dormRoomForm = { id: item.id, buildingName: item.buildingName, roomNo: item.roomNo, status: item.status };
        },
        resetDormRoomForm() {
            this.dormRoomForm = { id: null, buildingName: "", roomNo: "", status: 1 };
        },
        async saveDormRoom() {
            const payload = { buildingName: this.dormRoomForm.buildingName, roomNo: this.dormRoomForm.roomNo, status: this.dormRoomForm.status };
            if (this.dormRoomForm.id) {
                await this.apiCall(() => CampusGoApi.put(`/api/dorm-rooms/${this.dormRoomForm.id}`, payload), "宿舍号已更新");
            } else {
                await this.apiCall(() => CampusGoApi.post("/api/dorm-rooms", payload), "宿舍号已新增");
            }
            this.resetDormRoomForm();
            await this.loadManageDormRooms();
        },
        openAnnouncement(item) {
            this.selectedAnnouncement = item;
            this.announcementModal.show();
        },
        roleText(role) {
            return { STUDENT: "学生", TEACHER: "教师", ADMIN: "管理员" }[role] || role;
        },
        leaveStatusText(status) {
            return { PENDING: "待审核", APPROVED: "通过", REJECTED: "不通过", CANCELED: "已撤销", RETURNED: "已销假" }[status] || status;
        },
        repairStatusText(status) {
            return { PENDING: "待审核", APPROVED: "通过", REJECTED: "不通过", CANCELED: "已撤销", REPAIRING: "维修中", COMPLETED: "已完成", RATED: "已评价" }[status] || status;
        },
        repairReviewText(item) {
            if (item.score == null && !item.comment) {
                return "-";
            }
            const score = item.score == null ? "未评分" : `${item.score} 分`;
            return item.comment ? `${score} · ${item.comment}` : score;
        },
        filteredManageMajors() {
            if (!this.classForm.collegeName) {
                return [];
            }
            return this.manageMajors.filter((item) => item.collegeName === this.classForm.collegeName && item.status === 1);
        },
        heroSlides() {
            if (this.carousels.length > 0) {
                return this.carousels;
            }
            return [{
                id: "fallback",
                title: "校园事务，一站抵达。",
                subtitle: "公告查看、请假申请、公寓报修和事务审核集中处理，让日常校园服务更清晰、更轻快。",
                imageUrl: "/assets/campus-hero.png"
            }];
        },
        stateClass(status) {
            if (["PENDING", "REPAIRING"].includes(status)) return "pending";
            if (["APPROVED", "RETURNED", "COMPLETED", "RATED"].includes(status)) return "ok";
            if (["REJECTED"].includes(status)) return "bad";
            return "muted";
        },
        formatDate(value) {
            if (!value) {
                return "-";
            }
            return String(value).replace("T", " ").slice(0, 16);
        },
        toLocalDateTime(value) {
            return value ? `${value}:00` : null;
        },
        totalPages(data, query) {
            const pageSize = Number(query.pageSize || 10);
            return Math.max(1, Math.ceil((data.total || 0) / pageSize));
        },
        hasPages(data, query) {
            return this.totalPages(data, query) > 1;
        },
        pageText(data, query) {
            return `第 ${query.page || 1} / ${this.totalPages(data, query)} 页`;
        },
        async changePage(type, delta) {
            const map = {
                announcements: { query: this.announcementQuery, data: this.announcements, loader: this.loadAnnouncements },
                leaves: { query: this.leaveQuery, data: this.leaves, loader: this.loadMyLeaves },
                repairs: { query: this.repairQuery, data: this.repairs, loader: this.loadMyRepairs },
                auditLeaves: { query: this.auditLeaveQuery, data: this.auditLeaves, loader: this.loadAuditLeaves },
                auditRepairs: { query: this.auditRepairQuery, data: this.auditRepairs, loader: this.loadAuditRepairs }
            };
            const item = map[type];
            const current = item.query.page || 1;
            const next = Math.min(Math.max(1, current + delta), this.totalPages(item.data, item.query));
            if (next === current) {
                return;
            }
            item.query.page = next;
            await item.loader.call(this);
        },
        confirmAction(message, options = {}) {
            return new Promise((resolve) => {
                this.confirmation = {
                    title: options.title || "确认操作",
                    message,
                    confirmText: options.confirmText || "确认",
                    danger: Boolean(options.danger),
                    resolve
                };
                this.confirmationModal.show();
            });
        },
        closeConfirmation(confirmed) {
            const resolve = this.confirmation.resolve;
            this.confirmation.resolve = null;
            this.confirmationModal.hide();
            if (resolve) {
                resolve(confirmed);
            }
        },
        showToast(message, type = "success") {
            this.toast = { message, type };
            window.clearTimeout(this.toastTimer);
            this.toastTimer = window.setTimeout(() => {
                this.toast = { message: "", type: "success" };
            }, 2600);
        },
        refreshIcons() {
            if (window.lucide) {
                window.lucide.createIcons();
            }
        }
    }
}).mount("#app");
