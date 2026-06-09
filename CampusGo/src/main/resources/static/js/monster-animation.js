(function () {
    const engine = {
        state: {
            mouseX: window.innerWidth / 2,
            mouseY: window.innerHeight / 2,
            isTyping: false,
            showPassword: false,
            passwordLength: 0,
            isLookingAtEachOther: false,
            isPurplePeeking: false,
            isPurpleBlinking: false,
            isBlackBlinking: false
        },
        elements: {},
        initialized: false,
        frameId: null,
        peekTimer: null,
        blinkTimers: [],

        init(domSelectors) {
            this.elements = {
                charPurple: document.querySelector(domSelectors.charPurple),
                facePurple: document.querySelector(domSelectors.facePurple),
                charBlack: document.querySelector(domSelectors.charBlack),
                faceBlack: document.querySelector(domSelectors.faceBlack),
                charOrange: document.querySelector(domSelectors.charOrange),
                faceOrange: document.querySelector(domSelectors.faceOrange),
                charYellow: document.querySelector(domSelectors.charYellow),
                faceYellow: document.querySelector(domSelectors.faceYellow),
                mouthYellow: document.querySelector(domSelectors.mouthYellow)
            };

            if (!this.elements.charPurple || this.initialized) {
                return;
            }

            this.initialized = true;
            window.addEventListener("mousemove", (event) => {
                this.state.mouseX = event.clientX;
                this.state.mouseY = event.clientY;
            });
            this.startBlinkLoops();
            this.startPeekLoop();
            this.render();
        },

        setEmailFocus(isFocused) {
            this.state.isTyping = isFocused;
            if (isFocused) {
                this.state.isLookingAtEachOther = true;
                window.setTimeout(() => {
                    this.state.isLookingAtEachOther = false;
                }, 800);
            }
        },

        updatePasswordState(length, isVisible) {
            this.state.passwordLength = Number(length || 0);
            this.state.showPassword = Boolean(isVisible);
        },

        startBlinkLoops() {
            const scheduleBlink = (setBlinkState, minMs, maxMs) => {
                const timer = window.setTimeout(() => {
                    setBlinkState(true);
                    const closeTimer = window.setTimeout(() => {
                        setBlinkState(false);
                        scheduleBlink(setBlinkState, minMs, maxMs);
                    }, 150);
                    this.blinkTimers.push(closeTimer);
                }, Math.random() * (maxMs - minMs) + minMs);
                this.blinkTimers.push(timer);
            };

            scheduleBlink((value) => {
                this.state.isPurpleBlinking = value;
            }, 3000, 7000);
            scheduleBlink((value) => {
                this.state.isBlackBlinking = value;
            }, 3000, 7000);
        },

        startPeekLoop() {
            this.peekTimer = window.setInterval(() => {
                if (this.state.passwordLength > 0 && this.state.showPassword) {
                    this.state.isPurplePeeking = true;
                    window.setTimeout(() => {
                        this.state.isPurplePeeking = false;
                    }, 800);
                }
            }, 3500);
        },

        utils: {
            calculatePosition(element, mouseX, mouseY) {
                if (!element) return { faceX: 0, faceY: 0, bodySkew: 0 };
                const rect = element.getBoundingClientRect();
                const centerX = rect.left + rect.width / 2;
                const centerY = rect.top + rect.height / 3;
                const deltaX = mouseX - centerX;
                const deltaY = mouseY - centerY;

                return {
                    faceX: Math.max(-15, Math.min(15, deltaX / 20)),
                    faceY: Math.max(-10, Math.min(10, deltaY / 30)),
                    bodySkew: Math.max(-6, Math.min(6, -deltaX / 120))
                };
            },

            calculatePupil(eyeRect, maxDist, mouseX, mouseY, forceLookX, forceLookY) {
                if (forceLookX !== undefined && forceLookY !== undefined) {
                    return { x: forceLookX, y: forceLookY };
                }
                const centerX = eyeRect.left + eyeRect.width / 2;
                const centerY = eyeRect.top + eyeRect.height / 2;
                const deltaX = mouseX - centerX;
                const deltaY = mouseY - centerY;
                const distance = Math.min(Math.sqrt(deltaX ** 2 + deltaY ** 2), maxDist);
                const angle = Math.atan2(deltaY, deltaX);

                return {
                    x: Math.cos(angle) * distance,
                    y: Math.sin(angle) * distance
                };
            }
        },

        render() {
            const { state, elements, utils } = this;
            const {
                mouseX,
                mouseY,
                isTyping,
                showPassword,
                passwordLength,
                isLookingAtEachOther,
                isPurplePeeking,
                isPurpleBlinking,
                isBlackBlinking
            } = state;

            const passActive = passwordLength > 0 && showPassword;
            const typingOrHidden = isTyping || passActive;
            const pPos = utils.calculatePosition(elements.charPurple, mouseX, mouseY);
            const bPos = utils.calculatePosition(elements.charBlack, mouseX, mouseY);
            const oPos = utils.calculatePosition(elements.charOrange, mouseX, mouseY);
            const yPos = utils.calculatePosition(elements.charYellow, mouseX, mouseY);

            if (elements.charPurple && elements.facePurple) {
                elements.charPurple.style.height = typingOrHidden ? "304px" : "304px";
                elements.charPurple.style.transform = passActive
                    ? `translate(-22px, -12px) rotate(-5deg) skewX(${pPos.bodySkew - 4}deg)`
                    : typingOrHidden
                        ? `skewX(${pPos.bodySkew - 8}deg) translateX(22px)`
                        : `skewX(${pPos.bodySkew}deg)`;
                elements.facePurple.style.left = passActive ? "50px" : isLookingAtEachOther ? "68px" : `${56 + pPos.faceX}px`;
                elements.facePurple.style.top = passActive ? "28px" : isLookingAtEachOther ? "72px" : `${30 + pPos.faceY}px`;
            }

            if (elements.charBlack && elements.faceBlack) {
                elements.charBlack.style.transform = passActive
                    ? `translate(16px, -10px) rotate(4deg) skewX(${bPos.bodySkew}deg)`
                    : isLookingAtEachOther
                        ? `skewX(${bPos.bodySkew * 1.5 + 10}deg) translateX(20px)`
                        : typingOrHidden
                            ? `skewX(${bPos.bodySkew * 1.5}deg)`
                            : `skewX(${bPos.bodySkew}deg)`;
                elements.faceBlack.style.left = passActive ? "28px" : isLookingAtEachOther ? "42px" : `${30 + bPos.faceX}px`;
                elements.faceBlack.style.top = passActive ? "58px" : isLookingAtEachOther ? "24px" : `${54 + bPos.faceY}px`;
            }

            if (elements.charOrange && elements.faceOrange) {
                elements.charOrange.style.transform = passActive
                    ? `translate(-24px, 16px) rotate(-4deg) skewX(${oPos.bodySkew}deg)`
                    : `skewX(${oPos.bodySkew}deg)`;
                elements.faceOrange.style.left = passActive ? "66px" : `${72 + oPos.faceX}px`;
                elements.faceOrange.style.top = passActive ? "92px" : `${84 + oPos.faceY}px`;
            }

            if (elements.charYellow && elements.faceYellow && elements.mouthYellow) {
                elements.charYellow.style.transform = passActive
                    ? `translate(22px, 12px) rotate(5deg) skewX(${yPos.bodySkew}deg)`
                    : `skewX(${yPos.bodySkew}deg)`;
                elements.faceYellow.style.left = passActive ? "40px" : `${42 + yPos.faceX}px`;
                elements.faceYellow.style.top = passActive ? "42px" : `${44 + yPos.faceY}px`;
                elements.mouthYellow.style.left = passActive ? "34px" : `${36 + yPos.faceX}px`;
                elements.mouthYellow.style.top = passActive ? "92px" : `${94 + yPos.faceY}px`;
            }

            document.querySelectorAll(".eye-purple").forEach((eye) => {
                eye.style.height = isPurpleBlinking ? "2px" : "16px";
                const pupil = eye.querySelector(".pupil");
                if (pupil) pupil.style.opacity = isPurpleBlinking ? "0" : "1";
            });

            document.querySelectorAll(".eye-black").forEach((eye) => {
                eye.style.height = isBlackBlinking ? "2px" : "16px";
                const pupil = eye.querySelector(".pupil");
                if (pupil) pupil.style.opacity = isBlackBlinking ? "0" : "1";
            });

            const updatePupils = (selector, forceX, forceY) => {
                document.querySelectorAll(selector).forEach((pupil) => {
                    const container = pupil.parentElement;
                    const rect = container.getBoundingClientRect();
                    const maxDist = parseFloat(pupil.getAttribute("data-max-dist") || "5");
                    const pos = utils.calculatePupil(rect, maxDist, mouseX, mouseY, forceX, forceY);
                    pupil.style.transform = `translate(${pos.x}px, ${pos.y}px)`;
                });
            };

            const pForceX = passActive ? (isPurplePeeking ? 4 : -4) : isLookingAtEachOther ? 3 : undefined;
            const pForceY = passActive ? (isPurplePeeking ? 4 : -3) : isLookingAtEachOther ? 4 : undefined;
            updatePupils(".pupil-purple", pForceX, pForceY);

            const bForceX = passActive ? 4 : isLookingAtEachOther ? 0 : undefined;
            const bForceY = passActive ? -3 : isLookingAtEachOther ? -4 : undefined;
            updatePupils(".pupil-black", bForceX, bForceY);

            const oyForceX = passActive ? -4 : undefined;
            const oyForceY = passActive ? 2 : undefined;
            updatePupils(".pupil-orange", oyForceX, oyForceY);
            updatePupils(".pupil-yellow", oyForceX, oyForceY);

            this.frameId = window.requestAnimationFrame(() => this.render());
        }
    };

    window.CampusGoMonsterAnimation = engine;
})();
