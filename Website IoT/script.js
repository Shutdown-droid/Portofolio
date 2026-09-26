/* ============================================================================
   SMART AQUACULTURE V3 — SCRIPT.JS
   Vanilla JS murni, terstruktur modular via IIFE-scoped objects.
   Tanpa framework, tanpa build step, aman dibuka langsung lewat file:// atau
   hosting statis apa pun.

   BUILD LOG (progres pengerjaan bertahap):
     [x] Modul 1 — Utilities, Toast, Navbar, ScrollReveal
     [ ] Modul 2 — Ambient hero canvas (partikel latar hero)
     [ ] Modul 3 — Schedule Manager (jadwal pakan: tambah/edit/hapus/reset)
     [ ] Modul 4 — Servo Control (slider sudut katup + reset)
     [ ] Modul 5 — Telemetry (dashboard live monitor: TDS, pakan, baterai)
     [ ] Modul 6 — AquaBot chat widget + bootstrap akhir
   ========================================================================= */

(function () {
  'use strict';

  /* ==========================================================================
     MODUL 1A — UTILITIES
     Fungsi kecil lintas modul. Tidak menyimpan state apa pun sendiri.
     ========================================================================== */
  const $  = (sel, ctx = document) => ctx.querySelector(sel);
  const $$ = (sel, ctx = document) => Array.from(ctx.querySelectorAll(sel));

  const pad2 = (n) => String(n).padStart(2, '0');
  const clamp = (value, min, max) => Math.min(max, Math.max(min, value));
  const formatClock = (date) =>
    `${pad2(date.getHours())}:${pad2(date.getMinutes())}:${pad2(date.getSeconds())}`;

  /**
   * localStorage dibungkus try/catch — mode privat browser atau kuota penuh
   * bisa melempar error saat diakses. Kalau gagal, state cukup tidak
   * tersimpan antar-sesi; tidak boleh sampai mematikan seluruh skrip.
   */
  function readStorage(key, fallback) {
    try {
      const raw = localStorage.getItem(key);
      return raw ? JSON.parse(raw) : fallback;
    } catch (err) {
      return fallback;
    }
  }
  function writeStorage(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch (err) {
      /* Penyimpanan tidak tersedia — diabaikan, bukan error fatal. */
    }
  }

  /* ==========================================================================
     MODUL 1B — TOAST
     Notifikasi kecil di bawah layar. Dipakai modul lain (jadwal, servo,
     telemetry) untuk memberi umpan balik atas aksi pengguna.
     ========================================================================== */
  const Toast = (() => {
    let el = null;
    let hideTimer = null;

    function init() {
      el = $('#toast');
    }

    function show(message) {
      if (!el) return;
      el.textContent = message;
      el.classList.add('is-visible');
      clearTimeout(hideTimer);
      hideTimer = setTimeout(() => el.classList.remove('is-visible'), 2600);
    }

    return { init, show };
  })();

  /* ==========================================================================
     MODUL 1C — NAVBAR
     Toggle menu mobile; tertutup otomatis saat memilih link, klik di luar
     panel, atau menekan Escape.
     ========================================================================== */
  const Navbar = (() => {
    let nav, toggle, panel;

    function close() {
      nav.classList.remove('is-open');
      toggle.setAttribute('aria-expanded', 'false');
    }
    function open() {
      nav.classList.add('is-open');
      toggle.setAttribute('aria-expanded', 'true');
    }
    function handleToggleClick(e) {
      e.stopPropagation();
      nav.classList.contains('is-open') ? close() : open();
    }
    function handleDocumentClick(e) {
      if (nav.classList.contains('is-open') && !nav.contains(e.target)) close();
    }
    function handleKeydown(e) {
      if (e.key === 'Escape' && nav.classList.contains('is-open')) close();
    }

    function init() {
      nav = $('#navbar');
      toggle = $('#navToggle');
      panel = $('#navMobilePanel');
      if (!nav || !toggle || !panel) return;

      toggle.addEventListener('click', handleToggleClick);
      document.addEventListener('click', handleDocumentClick);
      document.addEventListener('keydown', handleKeydown);
      $$('.navbar__link', panel).forEach((link) => link.addEventListener('click', close));
    }

    return { init };
  })();

  /* ==========================================================================
     MODUL 1D — SCROLL REVEAL
     Entrance halus, sekali per elemen ".reveal", saat elemen masuk viewport.
     ========================================================================== */
  const ScrollReveal = (() => {
    function init() {
      const items = $$('.reveal');
      if (!items.length) return;

      if (!('IntersectionObserver' in window)) {
        items.forEach((el) => el.classList.add('is-visible'));
        return;
      }

      const observer = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting) {
              entry.target.classList.add('is-visible');
              observer.unobserve(entry.target);
            }
          });
        },
        { threshold: 0.15 }
      );

      items.forEach((el) => observer.observe(el));
    }

    return { init };
  })();

  /* ==========================================================================
     MODUL 2 — AMBIENT HERO CANVAS
     Partikel cahaya lembut melayang di background hero, seperti plankton
     bioluminescent. Dipakai sprite pra-render (bukan gradient dihitung ulang
     tiap frame) supaya ringan, dan berhenti otomatis saat tab disembunyikan
     atau prefers-reduced-motion aktif.
     ========================================================================== */
  const AmbientCanvas = (() => {
    let canvas, ctx, particles = [];
    let spriteMint, spriteCyan;
    let rafId = null;
    const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    function makeSprite(color) {
      const size = 220;
      const off = document.createElement('canvas');
      off.width = size;
      off.height = size;
      const octx = off.getContext('2d');
      const grad = octx.createRadialGradient(size / 2, size / 2, 0, size / 2, size / 2, size / 2);
      grad.addColorStop(0, color);
      grad.addColorStop(1, 'rgba(0,0,0,0)');
      octx.fillStyle = grad;
      octx.fillRect(0, 0, size, size);
      return off;
    }

    function resize() {
      const rect = canvas.getBoundingClientRect();
      const dpr = Math.min(window.devicePixelRatio || 1, 2);
      canvas.width = rect.width * dpr;
      canvas.height = rect.height * dpr;
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    }

    function seedParticles() {
      const rect = canvas.getBoundingClientRect();
      const count = rect.width < 700 ? 14 : 26;
      particles = Array.from({ length: count }, () => ({
        x: Math.random() * rect.width,
        y: Math.random() * rect.height,
        r: 40 + Math.random() * 90,
        vx: (Math.random() - 0.5) * 0.12,
        vy: -0.05 - Math.random() * 0.12,
        alpha: 0.05 + Math.random() * 0.12,
        mint: Math.random() > 0.4
      }));
    }

    function draw() {
      const rect = canvas.getBoundingClientRect();
      ctx.clearRect(0, 0, rect.width, rect.height);
      ctx.globalCompositeOperation = 'lighter';

      particles.forEach((p) => {
        p.x += p.vx;
        p.y += p.vy;
        if (p.y + p.r < 0) p.y = rect.height + p.r;
        if (p.x < -p.r) p.x = rect.width + p.r;
        if (p.x > rect.width + p.r) p.x = -p.r;

        ctx.globalAlpha = p.alpha;
        ctx.drawImage(p.mint ? spriteMint : spriteCyan, p.x - p.r, p.y - p.r, p.r * 2, p.r * 2);
      });

      ctx.globalAlpha = 1;
      ctx.globalCompositeOperation = 'source-over';
      rafId = requestAnimationFrame(draw);
    }

    function handleVisibility() {
      if (document.hidden) {
        cancelAnimationFrame(rafId);
      } else if (!reduceMotion) {
        rafId = requestAnimationFrame(draw);
      }
    }

    function handleResize() {
      resize();
      seedParticles();
    }

    function init() {
      canvas = $('#heroCanvas');
      if (!canvas) return;
      ctx = canvas.getContext('2d');
      spriteMint = makeSprite('rgba(143,224,190,0.55)');
      spriteCyan = makeSprite('rgba(127,207,218,0.5)');

      resize();
      seedParticles();

      if (reduceMotion) {
        draw();
        cancelAnimationFrame(rafId);
        return;
      }

      rafId = requestAnimationFrame(draw);
      window.addEventListener('resize', handleResize);
      document.addEventListener('visibilitychange', handleVisibility);
    }

    return { init };
  })();

  /* ==========================================================================
     MODUL 3 — SCHEDULE MANAGER
     CRUD jadwal pakan: tambah, ubah waktu/label, aktif/nonaktifkan, hapus,
     dan kembalikan ke bawaan (05:00 & 16:30). Tersimpan ke localStorage.

     Pola konfirmasi: alih-alih window.confirm() bawaan yang kaku dan merusak
     nuansa glass, tombol hapus/reset memakai pola "klik dua kali" — klik
     pertama mempersenjatai tombol (.is-armed, teks berubah), klik kedua
     dalam ~2.6 detik baru benar-benar mengeksekusi aksinya.
     ========================================================================== */
  const ScheduleManager = (() => {
    const STORAGE_KEY = 'aquaculture.schedules';
    const DEFAULT_SCHEDULES = [
      { id: 1, time: '05:00', label: 'Pagi', enabled: true },
      { id: 2, time: '16:30', label: 'Sore', enabled: true }
    ];
    const ARM_TIMEOUT = 2600;

    let schedules = [];
    let nextId = 1;
    let editingId = null;
    let listEl, formWrap, formEl, toggleFormBtn, cancelBtn, resetBtn;

    function persist() {
      writeStorage(STORAGE_KEY, schedules);
    }

    function getEnabledMinutes() {
      return schedules
        .filter((s) => s.enabled)
        .map((s) => {
          const [h, m] = s.time.split(':').map(Number);
          return h * 60 + m;
        })
        .sort((a, b) => a - b);
    }

    function escapeHtml(str) {
      const div = document.createElement('div');
      div.textContent = str;
      return div.innerHTML;
    }

    function render() {
      if (!schedules.length) {
        listEl.innerHTML = '<li class="schedule-empty">Belum ada jadwal — tambahkan salah satu di atas.</li>';
        return;
      }
      const sorted = [...schedules].sort((a, b) => a.time.localeCompare(b.time));
      listEl.innerHTML = sorted.map(renderRow).join('');
    }

    function renderRow(item) {
      const safeLabel = escapeHtml(item.label);

      if (editingId === item.id) {
        return `
          <li class="schedule-row" data-id="${item.id}">
            <span class="schedule-row__toggle" role="switch" aria-checked="${item.enabled}" data-action="toggle" tabindex="0"></span>
            <input class="schedule-row__input" type="time" value="${item.time}" data-field="time">
            <input class="schedule-row__input" type="text" value="${safeLabel}" maxlength="14" data-field="label">
            <div class="schedule-row__actions">
              <button class="icon-btn" type="button" data-action="save" aria-label="Simpan perubahan">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>
              </button>
              <button class="icon-btn" type="button" data-action="cancel-edit" aria-label="Batalkan perubahan">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M6 6l12 12M18 6 6 18"/></svg>
              </button>
            </div>
          </li>`;
      }

      return `
        <li class="schedule-row ${item.enabled ? '' : 'is-inactive'}" data-id="${item.id}">
          <span class="schedule-row__toggle" role="switch" aria-checked="${item.enabled}" data-action="toggle" tabindex="0" aria-label="Aktifkan atau nonaktifkan jadwal ${safeLabel}">
            <span class="schedule-row__toggle-knob"></span>
          </span>
          <span class="schedule-row__time">${item.time}</span>
          <span class="schedule-row__label">${safeLabel}</span>
          <span class="schedule-row__state">${item.enabled ? 'Aktif' : 'Nonaktif'}</span>
          <div class="schedule-row__actions">
            <button class="icon-btn" type="button" data-action="edit" aria-label="Ubah jadwal ${safeLabel}">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
            </button>
            <button class="icon-btn icon-btn--danger" type="button" data-action="delete" aria-label="Hapus jadwal ${safeLabel}">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 7h16M9 7V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2m2 0-1 13a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 7"/></svg>
            </button>
          </div>
        </li>`;
    }

    function armButton(button, label) {
      button.classList.add('is-armed');
      if (label) button.textContent = label;
      clearTimeout(button._armTimer);
      button._armTimer = setTimeout(() => disarmButton(button), ARM_TIMEOUT);
    }
    function disarmButton(button, label) {
      button.classList.remove('is-armed');
      if (label) button.textContent = label;
    }

    function handleListClick(e) {
      const actionEl = e.target.closest('[data-action]');
      if (!actionEl) return;
      const row = e.target.closest('.schedule-row');
      const id = row ? Number(row.dataset.id) : null;
      const action = actionEl.dataset.action;

      if (action === 'toggle' && id != null) {
        const item = schedules.find((s) => s.id === id);
        if (item) {
          item.enabled = !item.enabled;
          persist();
          render();
        }
      } else if (action === 'edit' && id != null) {
        editingId = id;
        render();
        const input = listEl.querySelector(`.schedule-row[data-id="${id}"] input[data-field="label"]`);
        if (input) input.focus();
      } else if (action === 'cancel-edit') {
        editingId = null;
        render();
      } else if (action === 'save' && id != null) {
        const timeInput = row.querySelector('input[data-field="time"]');
        const labelInput = row.querySelector('input[data-field="label"]');
        const item = schedules.find((s) => s.id === id);
        if (item && timeInput && timeInput.value) {
          item.time = timeInput.value;
          item.label = (labelInput.value.trim() || 'Jadwal').slice(0, 14);
          editingId = null;
          persist();
          render();
          Toast.show('Jadwal diperbarui.');
        }
      } else if (action === 'delete' && id != null) {
        if (actionEl.classList.contains('is-armed')) {
          schedules = schedules.filter((s) => s.id !== id);
          persist();
          render();
          Toast.show('Jadwal dihapus.');
        } else {
          armButton(actionEl);
          actionEl.setAttribute('aria-label', 'Klik sekali lagi untuk menghapus');
        }
      }
    }

    function handleListKeydown(e) {
      if ((e.key === 'Enter' || e.key === ' ') && e.target.matches('[data-action="toggle"]')) {
        e.preventDefault();
        e.target.click();
      }
    }

    function openForm() {
      formWrap.classList.add('is-open');
      toggleFormBtn.setAttribute('aria-expanded', 'true');
      toggleFormBtn.textContent = '– Tutup form';
      const timeField = $('#inputTime');
      if (timeField) timeField.focus();
    }
    function closeForm() {
      formWrap.classList.remove('is-open');
      toggleFormBtn.setAttribute('aria-expanded', 'false');
      toggleFormBtn.textContent = '+ Tambah';
      formEl.reset();
      $('#inputTime').value = '12:00';
    }

    function handleSubmit(e) {
      e.preventDefault();
      const time = $('#inputTime').value;
      const label = $('#inputLabel').value.trim().slice(0, 14);
      if (!time || !label) return;

      schedules.push({ id: nextId++, time, label, enabled: true });
      persist();
      render();
      closeForm();
      Toast.show(`Jadwal "${label}" ditambahkan.`);
    }

    function handleResetClick() {
      if (!resetBtn.classList.contains('is-armed')) {
        armButton(resetBtn, 'Yakin? klik lagi');
        return;
      }
      schedules = DEFAULT_SCHEDULES.map((s) => ({ ...s }));
      nextId = 3;
      editingId = null;
      persist();
      render();
      closeForm();
      disarmButton(resetBtn, 'Kembalikan bawaan');
      Toast.show('Jadwal dikembalikan ke bawaan (05:00 & 16:30).');
    }

    function init() {
      listEl = $('#scheduleList');
      formWrap = $('#scheduleForm');
      formEl = $('#addScheduleForm');
      toggleFormBtn = $('#btnToggleAddForm');
      cancelBtn = $('#btnCancelAdd');
      resetBtn = $('#btnResetSchedule');
      if (!listEl || !formEl) return;

      const stored = readStorage(STORAGE_KEY, null);
      schedules = Array.isArray(stored) && stored.length ? stored : DEFAULT_SCHEDULES.map((s) => ({ ...s }));
      nextId = schedules.reduce((max, s) => Math.max(max, s.id + 1), 1);

      listEl.addEventListener('click', handleListClick);
      listEl.addEventListener('keydown', handleListKeydown);
      toggleFormBtn.addEventListener('click', () => {
        formWrap.classList.contains('is-open') ? closeForm() : openForm();
      });
      cancelBtn.addEventListener('click', closeForm);
      formEl.addEventListener('submit', handleSubmit);
      resetBtn.addEventListener('click', handleResetClick);

      render();
    }

    return { init, getEnabledMinutes, getSchedules: () => schedules.map((s) => ({ ...s })) };
  })();

  /* ==========================================================================
     MODUL 4 — SERVO CONTROL
     Slider 0°–180° untuk katup servo, dial radial SVG, preset cepat, dan
     tombol reset ke posisi bawaan (0°, tertutup). Sudut terakhir disimpan
     ke localStorage.

     RADIUS harus persis sama dengan atribut r pada <circle id="servoArc">
     di index.html (r="78") — satu sumber kebenaran untuk perhitungan
     lingkaran, supaya tidak terulang bug "angka keliling perkiraan".
     ========================================================================== */
  const ServoControl = (() => {
    const STORAGE_KEY_ANGLE = 'aquaculture.servoAngle';
    const STORAGE_KEY_MODE = 'aquaculture.servoMode';
    const RADIUS = 78;
    const CIRCUMFERENCE = 2 * Math.PI * RADIUS;
    const AUTO_FEED_ANGLE = 180;
    const AUTO_FEED_HOLD_MS = 6000; // simulasi demo; perangkat asli ±45 detik

    const ICON_CLOSED = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="5" y="11" width="14" height="9" rx="2"/><path d="M8 11V8a4 4 0 0 1 8 0v3"/></svg>';
    const ICON_OPEN = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="5" y="11" width="14" height="9" rx="2"/><path d="M8 11V8a4 4 0 0 1 7.8-1.2"/></svg>';

    let slider, arc, angleValueEl, stateLabelEl, iconEl, presetButtons, resetBtn;
    let servoWrap, modeButtons, modeNoteEl, statModeEl;

    let mode = 'auto';       // 'auto' | 'manual'
    let manualAngle = 0;     // sudut pilihan pengguna, tersimpan, dipakai saat mode manual
    let displayedAngle = 0;  // sudut yang SEDANG tampil di dial (bisa beda saat auto-feed berjalan)
    let autoFeedTimer = null;

    function describeAngle(v) {
      if (v === 0) return { label: 'Tertutup', tone: null };
      if (v < 60) return { label: 'Sedikit terbuka', tone: 'is-warning' };
      if (v < 140) return { label: 'Setengah terbuka', tone: 'is-warning' };
      return { label: 'Terbuka penuh', tone: null };
    }

    /** Menggambar ulang dial+slider ke sudut tertentu. Murni presentasi —
        tidak memutuskan apakah boleh diubah (itu tugas applyManual/mode). */
    function renderAngle(angle) {
      displayedAngle = clamp(Math.round(Number(angle) || 0), 0, 180);
      slider.value = displayedAngle;
      angleValueEl.textContent = `${displayedAngle}°`;

      const fraction = displayedAngle / 180;
      arc.style.strokeDashoffset = String(CIRCUMFERENCE * (1 - fraction));

      const { label, tone } = describeAngle(displayedAngle);
      stateLabelEl.textContent = label;
      arc.classList.remove('is-warning', 'is-alert');
      if (tone) arc.classList.add(tone);

      iconEl.innerHTML = displayedAngle === 0 ? ICON_CLOSED : ICON_OPEN;
      presetButtons.forEach((btn) => {
        btn.classList.toggle('is-active', Number(btn.dataset.angle) === displayedAngle);
      });
    }

    /** Dipanggil dari slider/preset — hanya berefek saat mode Manual aktif,
        karena elemen kontrolnya sendiri dinonaktifkan (pointer-events:none)
        saat mode Otomatis, jadi ini pengaman lapis kedua. */
    function applyManual(rawValue) {
      if (mode !== 'manual') return;
      manualAngle = clamp(Math.round(Number(rawValue) || 0), 0, 180);
      renderAngle(manualAngle);
      writeStorage(STORAGE_KEY_ANGLE, manualAngle);
    }

    function updateModeUI() {
      modeButtons.forEach((btn) => {
        btn.setAttribute('aria-checked', String(btn.dataset.mode === mode));
      });
      servoWrap.classList.toggle('is-auto-mode', mode === 'auto');
      modeNoteEl.textContent = mode === 'auto'
        ? 'Servo mengikuti jadwal pakan secara otomatis — pindah ke Manual untuk kendali langsung saat ini juga.'
        : 'Mode manual aktif — geser atau pilih preset untuk mengatur sudut katup secara langsung.';
      if (statModeEl) {
        statModeEl.textContent = mode === 'auto' ? 'Otomatis · NTP tersinkron' : 'Manual · kendali langsung';
      }
    }

    function setMode(newMode, { persistValue = true, silent = false } = {}) {
      const next = newMode === 'manual' ? 'manual' : 'auto';
      if (next === mode && persistValue) return;
      mode = next;
      updateModeUI();
      if (persistValue) writeStorage(STORAGE_KEY_MODE, mode);

      clearTimeout(autoFeedTimer);
      renderAngle(mode === 'manual' ? manualAngle : 0);

      if (!silent) {
        Toast.show(mode === 'auto' ? 'Mode Otomatis aktif — servo mengikuti jadwal.' : 'Mode Manual aktif — Anda mengendalikan servo langsung.');
      }
    }

    function handleReset() {
      manualAngle = 0;
      writeStorage(STORAGE_KEY_ANGLE, 0);
      if (mode === 'manual') renderAngle(0);
      Toast.show('Servo dikembalikan ke posisi bawaan (0°, tertutup).');
    }

    /**
     * Dipanggil oleh Telemetry saat jam sistem mencocokkan salah satu jadwal
     * aktif. Sengaja DIABAIKAN kalau mode sedang Manual — operator sedang
     * memegang kendali langsung, jadwal otomatis tidak boleh mengambil alih.
     */
    function triggerAutoFeed() {
      if (mode !== 'auto') return;
      clearTimeout(autoFeedTimer);
      renderAngle(AUTO_FEED_ANGLE);
      Toast.show('Jadwal pakan aktif — servo membuka otomatis.');
      autoFeedTimer = setTimeout(() => {
        renderAngle(0);
        Toast.show('Siklus pemberian pakan selesai — servo tertutup kembali.');
      }, AUTO_FEED_HOLD_MS);
    }

    /** Dipakai AquaBot untuk menjawab pertanyaan tentang kondisi servo saat ini. */
    function getState() {
      return { angle: displayedAngle, mode, manualAngle };
    }

    function init() {
      slider = $('#servoSlider');
      arc = $('#servoArc');
      angleValueEl = $('#servoAngleValue');
      stateLabelEl = $('#servoStateLabel');
      iconEl = $('#servoIcon');
      presetButtons = $$('.servo__presets .chip');
      resetBtn = $('#btnResetServo');
      servoWrap = $('#servoControls');
      modeButtons = $$('#servoModeSwitch .mode-switch__btn');
      modeNoteEl = $('#servoModeNote');
      statModeEl = $('#statMode');
      if (!slider || !arc) return;

      arc.style.strokeDasharray = String(CIRCUMFERENCE);

      slider.addEventListener('input', () => applyManual(slider.value));
      presetButtons.forEach((btn) => {
        btn.addEventListener('click', () => applyManual(btn.dataset.angle));
      });
      resetBtn.addEventListener('click', handleReset);
      modeButtons.forEach((btn) => {
        btn.addEventListener('click', () => setMode(btn.dataset.mode));
      });

      manualAngle = clamp(Number(readStorage(STORAGE_KEY_ANGLE, 0)) || 0, 0, 180);
      const savedMode = readStorage(STORAGE_KEY_MODE, 'auto');
      setMode(savedMode === 'manual' ? 'manual' : 'auto', { persistValue: false, silent: true });
    }

    return { init, getState, triggerAutoFeed };
  })();

  /* ==========================================================================
     MODUL 5 — TELEMETRY
     Dashboard Live Monitor + status hero: TDS air, stok pakan, baterai, dan
     hitung mundur ke jadwal berikutnya (dibaca langsung dari ScheduleManager
     — arah data satu jalur saja, supaya tidak ada dependensi silang antar
     modul yang rawan bug urutan). Juga memantau jam sistem tiap detik dan
     memicu ServoControl.triggerAutoFeed() persis saat jadwal aktif tercapai.

     ══════════════════════════════════════════════════════════════════════
     SUMBER DATA — SIMULASI vs PERANGKAT ASLI (baca ini sebelum deploy!)
     ══════════════════════════════════════════════════════════════════════
     Semua angka di dashboard ini SAAT INI berasal dari simulateReading() di
     bawah — random-walk buatan, BUKAN pembacaan sensor sungguhan dari ESP32.
     Ini satu-satunya fungsi yang perlu diganti agar dashboard membaca data
     ASLI dari alat fisik. Tiga jalur paling umum untuk fisik→web:

       1) ESP32 jadi web server sendiri (ESPAsyncWebServer / WebServer.h),
          expose endpoint mis. GET /status → balas JSON:
          { "tds":342, "feed":73, "battery":88, "servoAngle":0 }
          lalu di sini cukup: const res = await fetch('http://<IP-ESP32>/status');
          Paling simpel untuk demo di jaringan WiFi yang sama, tanpa akun cloud.

       2) Firebase Realtime Database — ESP32 push data via HTTP/Firebase
          library, browser subscribe pakai Firebase JS SDK (bisa diakses dari
          mana saja, ada tier gratis).

       3) MQTT broker (mis. HiveMQ Cloud) — ESP32 publish topic sensor,
          browser subscribe via MQTT-over-WebSocket (browser tidak bisa MQTT
          TCP mentah).

     Pilih salah satu, lalu ganti ISI simulateReading() dengan pemanggilan
     nyata ke sana. Struktur return-nya HARUS tetap { tds, feed, battery }
     supaya renderTds/renderFeed/renderBattery di bawah tidak perlu diubah.
     ========================================================================== */
  const Telemetry = (() => {
    const TDS_RADIUS = 72;
    const TDS_CIRCUMFERENCE = 2 * Math.PI * TDS_RADIUS;
    const TDS_WARN = 380;
    const TDS_ALERT = 500;
    const TDS_GAUGE_MAX = 620;
    const BATTERY_FILL_MAX_WIDTH = 32;

    // Tandai sumber data secara eksplisit — dibaca oleh badge "Data simulasi"
    // di HTML (#dataSourceBadge) dan oleh AquaBot saat menjawab pertanyaan
    // kondisi alat, supaya tidak ada klaim "live dari alat asli" yang keliru.
    const DATA_MODE = 'simulated'; // ganti ke 'live' setelah simulateReading() diganti data asli

    let state = { tds: 342, feed: 73, battery: 88, alertZone: 'safe' };
    let els = {};
    let lastFiredScheduleKey = null;

    function cacheEls() {
      els = {
        tdsArc: $('#tdsArc'),
        tdsValue: $('#tdsValue'),
        tdsState: $('#tdsState'),
        feedSilo: $('#feedSilo'),
        feedValue: $('#feedValue'),
        batteryFill: $('#batteryFill'),
        batteryValue: $('#batteryValue'),
        batteryState: $('#batteryState'),
        batteryIndicator: $('#batteryIndicator'),
        nextFeedValue: $('#nextFeedValue'),
        nextFeedLabel: $('#nextFeedLabel'),
        nextFeedBar: $('#nextFeedBar'),
        statusLogText: $('#statusLogText'),
        statusLogTime: $('#statusLogTime'),
        statPower: $('#statPower'),
        statWater: $('#statWater'),
        statFeed: $('#statFeed'),
        statTimestamp: $('#statTimestamp'),
        dataSourceBadge: $('#dataSourceBadge')
      };
    }

    function isDaylightHour() {
      const h = new Date().getHours();
      return h >= 6 && h < 17;
    }

    /**
     * SATU-SATUNYA titik yang perlu diganti untuk data sungguhan.
     * Sekarang: random-walk lembut supaya angka terasa hidup di demo.
     * Nanti: ganti isinya jadi `await fetch(...)` ke sumber Anda (lihat
     * catatan arsitektur di komentar atas modul ini).
     */
    function simulateReading(prev) {
      return {
        tds: clamp(prev.tds + (Math.random() - 0.5) * 26, 180, 640),
        feed: clamp(prev.feed - Math.random() * 0.4, 4, 100),
        battery: clamp(prev.battery + (isDaylightHour() ? 1 : -1) * Math.random() * 1.2, 12, 100)
      };
    }

    function renderTds() {
      const fraction = clamp(state.tds / TDS_GAUGE_MAX, 0, 1);
      els.tdsArc.style.strokeDashoffset = String(TDS_CIRCUMFERENCE * (1 - fraction));
      els.tdsValue.textContent = Math.round(state.tds);

      els.tdsArc.classList.remove('is-warning', 'is-alert');
      let zone = 'safe';
      if (state.tds >= TDS_ALERT) {
        els.tdsArc.classList.add('is-alert');
        zone = 'alert';
      } else if (state.tds >= TDS_WARN) {
        els.tdsArc.classList.add('is-warning');
        zone = 'warning';
      }

      els.tdsState.textContent =
        zone === 'alert' ? 'Melebihi ambang aman' :
        zone === 'warning' ? 'Mendekati ambang · perlu diawasi' :
        'Aman · ambang 500 PPM';

      els.statWater.textContent = `${zone === 'alert' ? 'Waspada' : 'Optimal'} · ${Math.round(state.tds)} PPM`;

      if (zone === 'alert' && state.alertZone !== 'alert') {
        Toast.show('Peringatan: TDS air melewati ambang aman 500 PPM.');
        logEvent('TDS melewati ambang aman — periksa kualitas air segera.', true);
      }
      state.alertZone = zone;
    }

    function renderFeed() {
      const pct = clamp(Math.round(state.feed), 0, 100);
      els.feedSilo.style.height = `${pct}%`;
      els.feedValue.textContent = `${pct}%`;
      els.statFeed.textContent = `${pct}% kapasitas`;
    }

    function renderBattery() {
      const pct = clamp(Math.round(state.battery), 0, 100);
      els.batteryFill.setAttribute('width', String((pct / 100) * BATTERY_FILL_MAX_WIDTH));
      els.batteryValue.textContent = `${pct}%`;

      const charging = isDaylightHour();
      els.batteryIndicator.classList.toggle('is-charging', charging);
      els.batteryState.textContent = charging ? 'mengisi · solar aktif' : 'memakai cadangan baterai';
      els.statPower.textContent = `${pct}% · ${charging ? 'Solar aktif' : 'Baterai'}`;
    }

    /**
     * Hitung jadwal berikutnya dari ScheduleManager. Dipisah dari
     * renderNextFeed() supaya bisa dipakai ulang oleh AquaBot (getNextFeedInfo)
     * tanpa menyentuh DOM sama sekali.
     */
    function computeNextFeed() {
      const now = new Date();
      const nowMinutes = now.getHours() * 60 + now.getMinutes();
      const enabled = ScheduleManager.getEnabledMinutes();

      if (!enabled.length) {
        return { time: '—', fraction: 0, hasSchedule: false };
      }

      let next = enabled.find((t) => t > nowMinutes);
      let previous;
      if (next === undefined) {
        next = enabled[0];
        previous = enabled[enabled.length - 1] - 24 * 60;
      } else {
        const before = enabled.filter((t) => t <= nowMinutes);
        previous = before.length ? before[before.length - 1] : enabled[enabled.length - 1] - 24 * 60;
      }

      const fraction = (nowMinutes - previous) / (next - previous);
      const displayMinute = ((next % (24 * 60)) + 24 * 60) % (24 * 60);
      const h = Math.floor(displayMinute / 60);
      const m = displayMinute % 60;

      return { time: `${pad2(h)}:${pad2(m)}`, fraction: clamp(fraction, 0, 1), hasSchedule: true };
    }

    function renderNextFeed() {
      const info = computeNextFeed();
      els.nextFeedValue.textContent = info.time;
      els.nextFeedLabel.textContent = info.hasSchedule ? 'jadwal berikutnya · WIB' : 'tidak ada jadwal aktif';
      els.nextFeedBar.style.width = `${(info.fraction * 100).toFixed(0)}%`;
    }

    /**
     * Dicek tiap detik: kalau jam:menit sekarang PERSIS cocok salah satu
     * jadwal aktif, dan belum pernah memicu untuk menit yang sama hari ini,
     * suruh ServoControl membuka otomatis. triggerAutoFeed() sendiri yang
     * memutuskan untuk mengabaikan ini kalau servo sedang mode Manual.
     */
    function checkScheduleTrigger() {
      const now = new Date();
      const nowMinutes = now.getHours() * 60 + now.getMinutes();
      const enabled = ScheduleManager.getEnabledMinutes();
      if (!enabled.includes(nowMinutes)) return;

      const key = `${now.toDateString()}|${nowMinutes}`;
      if (key === lastFiredScheduleKey) return;
      lastFiredScheduleKey = key;

      ServoControl.triggerAutoFeed();
    }

    function logEvent(text, isAlert) {
      els.statusLogText.innerHTML = `<strong>${isAlert ? 'Perhatian' : 'Normal'}</strong> — ${text}`;
    }

    function tickClock() {
      els.statusLogTime.textContent = formatClock(new Date());
      els.statTimestamp.textContent = 'diperbarui baru saja';
      renderNextFeed();
      checkScheduleTrigger();
    }

    function refreshReadings() {
      state = { ...state, ...simulateReading(state) };
      renderTds();
      renderFeed();
      renderBattery();

      if (state.alertZone !== 'alert') {
        logEvent('sinkronisasi sensor berhasil, seluruh pembacaan stabil.', false);
      }
    }

    /** Dipakai AquaBot — snapshot lengkap kondisi alat saat ini, tanpa DOM. */
    function getSnapshot() {
      return {
        tds: state.tds,
        feed: state.feed,
        battery: state.battery,
        alertZone: state.alertZone,
        charging: isDaylightHour(),
        dataMode: DATA_MODE
      };
    }

    function init() {
      cacheEls();
      if (!els.tdsArc) return;

      els.tdsArc.style.strokeDasharray = String(TDS_CIRCUMFERENCE);
      if (els.dataSourceBadge) {
        els.dataSourceBadge.textContent = DATA_MODE === 'live' ? '● Data langsung dari alat' : '◔ Data simulasi (demo)';
        els.dataSourceBadge.classList.toggle('is-live', DATA_MODE === 'live');
      }

      renderTds();
      renderFeed();
      renderBattery();
      tickClock();

      setInterval(tickClock, 1000);
      setInterval(refreshReadings, 4200);
    }

    return { init, getSnapshot, getNextFeedInfo: computeNextFeed };
  })();

  /* ==========================================================================
     MODUL 6 — AQUABOT CHAT WIDGET
     CATATAN PENTING: memanggil API Claude/LLM langsung dari browser TIDAK
     aman (API key akan terekspos ke siapa pun yang membuka DevTools) dan
     akan diblokir kebijakan CORS di server Anthropic. Karena itu AquaBot di
     sini memakai mesin jawaban lokal berbasis pencocokan kata kunci —
     berjalan 100% di browser, tanpa server, tanpa API key yang bisa bocor.

     Untuk jawaban AI yang benar-benar generatif, sambungkan getReply() ke
     backend Anda sendiri (mis. endpoint /api/aquabot) yang menyimpan API
     key dengan aman di sisi server.
     ========================================================================== */
  const AquaBot = (() => {
    /* ---- Basis pengetahuan statis: konsep & fakta yang tidak berubah ---- */
    const KNOWLEDGE_BASE = [
      {
        keywords: ['tds', 'kualitas air', 'amonia', 'ppm'],
        reply: 'Sensor TDS (Total Dissolved Solids) mengukur jumlah partikel terlarut dalam air — termasuk amonia, nitrat, dan mineral. Untuk budidaya ikan, batas amannya di bawah 500 PPM. Kalau melewati itu, ikan bisa stres atau keracunan. 🐟 (Tanya "TDS sekarang berapa?" kalau mau lihat angka live-nya.)'
      },
      {
        keywords: ['esp32', 'mikrokontroler', 'otak', 'chip'],
        reply: 'ESP32 dipilih karena punya WiFi & Bluetooth bawaan, harga terjangkau, dan komunitasnya besar. Dengan dual-core 240MHz, ESP32 cukup kuat menjalankan seluruh logika sistem ini sendirian. ⚡'
      },
      {
        keywords: ['solar', 'surya', 'panel', 'energi', 'listrik', 'pln'],
        reply: 'Panel surya 10WP dipadukan Aki 12V/5Ah membuat sistem ini benar-benar off-grid. Solar Charge Controller mengatur pengisian otomatis, jadi baterai tidak overcharge dan tetap awet meski dipakai bertahun-tahun. ☀️'
      },
      {
        keywords: ['servo', 'katup', 'motor'],
        reply: 'Motor servo bertindak sebagai katup presisi (0°–180°) yang mengatur aliran pakan dari wadah ke corong distribusi. Ada dua mode: Otomatis (ikut jadwal) dan Manual (dikendalikan langsung). Tanya "servo sekarang posisi apa?" untuk lihat kondisinya sekarang. 🔧'
      },
      {
        keywords: ['cara kerja', 'bagaimana cara', 'alur', 'proses kerja'],
        reply: 'Alurnya: panel surya isi aki lewat SCC → ESP32 sinkron waktu via NTP → saat jadwal tiba (mode Otomatis), servo buka katup → pakan jatuh ke motor DC yang berputar cepat → pakan tersebar merata secara sentrifugal. Semua otomatis tanpa sentuhan manusia, kecuali Anda pindah ke mode Manual. 🔄'
      },
      {
        keywords: ['ultrasonik', 'hc-sr04', 'sensor jarak'],
        reply: 'Sensor ultrasonik HC-SR04 mengukur jarak ke permukaan pakan pakai pantulan gelombang suara — tanpa menyentuh pakan sama sekali. Dari situ sistem bisa mengestimasi persentase sisa stok tanpa mengotori sensor. 📡'
      },
      {
        keywords: ['budidaya', 'tips', 'ikan', 'kolam'],
        reply: 'Beberapa dasar budidaya yang baik: jaga kualitas air (TDS, pH, suhu), beri pakan di jam konsisten, hindari overfeeding karena sisa pakan mencemari air, dan lakukan pergantian air berkala. Sistem otomatis ini membantu menjaga konsistensi jadwalnya. 🐠'
      },
      {
        keywords: ['siapa', 'developer', 'pembuat', 'sekolah'],
        reply: 'Sistem ini dikembangkan oleh Arsad Azami Nursamal, jurusan Sistem Informasi Jaringan dan Aplikasi (SIJA), SMKN 2 Yogyakarta — proyek yang menggabungkan jaringan internet, hardware, dan software jadi satu sistem budidaya cerdas. 🎓'
      }
    ];
    const FALLBACK_REPLY = 'Pertanyaan menarik! Saya bisa membahas konsep sistem ini (ESP32, sensor, energi surya) atau kondisinya SEKARANG (coba tanya "kondisi alat sekarang gimana?"). Coba tanya lebih spesifik ya. 🤖';

    let fab, panel, closeBtn, messagesEl, form, input, suggestions, aquabotRoot;
    let hasGreeted = false;

    function scoreMatch(text, keywords) {
      const lower = text.toLowerCase();
      return keywords.reduce((score, kw) => (lower.includes(kw) ? score + 1 : score), 0);
    }

    /** Kata sinyal "tanya kondisi SEKARANG", pembeda dari pertanyaan konsep umum. */
    function hasLiveSignal(lower) {
      return /(sekarang|saat ini|current|live|langsung|lagi (berapa|posisi|gimana)|barusan)/.test(lower);
    }

    /**
     * Jawaban DINAMIS dari kondisi alat — dibaca langsung dari Telemetry,
     * ServoControl, dan ScheduleManager setiap kali ditanya, sehingga selalu
     * mencerminkan angka yang SEDANG tampil di dashboard, bukan hafalan
     * statis. Return null kalau pertanyaan bukan soal kondisi live, supaya
     * getReply() lanjut mencoba basis pengetahuan konseptual.
     */
    function getLiveStateReply(rawText) {
      const lower = rawText.toLowerCase();
      const snap = Telemetry.getSnapshot();
      const servo = ServoControl.getState();
      const note = snap.dataMode === 'simulated' ? ' (data simulasi demo — belum tersambung sensor fisik)' : '';

      const asksOverview = /kondisi (alat|sistem)|status (alat|sistem|keseluruhan)|semua(nya)? (gimana|bagaimana)|ringkasan/.test(lower);
      if (asksOverview) {
        const zone = snap.alertZone === 'alert' ? 'MELEBIHI ambang aman' : snap.alertZone === 'warning' ? 'mendekati ambang' : 'aman';
        return `Kondisi alat saat ini:${note}\n`
          + `💧 TDS: ${Math.round(snap.tds)} PPM (${zone})\n`
          + `🌾 Stok pakan: ${Math.round(snap.feed)}%\n`
          + `🔋 Baterai: ${Math.round(snap.battery)}% (${snap.charging ? 'mengisi solar' : 'pakai cadangan'})\n`
          + `🔧 Servo: ${servo.angle}° · mode ${servo.mode === 'auto' ? 'Otomatis' : 'Manual'}`;
      }

      if (hasLiveSignal(lower) && /tds|kualitas air|amonia|ppm/.test(lower)) {
        const verdict = snap.alertZone === 'alert'
          ? 'ini SUDAH melewati ambang aman 500 PPM — segera periksa kolam!'
          : snap.alertZone === 'warning' ? 'mendekati ambang aman, perlu diawasi.' : 'masih aman.';
        return `TDS air ${Math.round(snap.tds)} PPM${note} — ${verdict}`;
      }

      if (hasLiveSignal(lower) && /baterai|daya|aki/.test(lower)) {
        return `Baterai ${Math.round(snap.battery)}%${note}, ${snap.charging ? 'sedang mengisi dari panel surya ☀️.' : 'sedang memakai cadangan (di luar jam matahari).'}`;
      }

      if (/stok pakan|sisa pakan|berapa pakan/.test(lower)) {
        return `Stok pakan tersisa ${Math.round(snap.feed)}%${note} dari kapasitas wadah.`;
      }

      if (/(posisi|sudut).*(servo|katup)|servo.*(posisi|sudut|sekarang)|katup.*(sekarang|posisi)/.test(lower)) {
        return `Servo sekarang di sudut ${servo.angle}°, mode ${servo.mode === 'auto' ? 'Otomatis (mengikuti jadwal)' : 'Manual (dikendalikan langsung)'}.`;
      }

      if (/mode (apa|sekarang)|otomatis atau manual/.test(lower)) {
        return `Mode servo saat ini: ${servo.mode === 'auto' ? 'Otomatis — mengikuti jadwal pakan.' : 'Manual — dikendalikan langsung lewat slider.'}`;
      }

      if (/jadwal (berikutnya|selanjutnya)|kapan.*pakan|jam berapa.*pakan/.test(lower)) {
        const info = Telemetry.getNextFeedInfo();
        return info.hasSchedule
          ? `Jadwal pakan berikutnya pukul ${info.time} WIB.`
          : 'Belum ada jadwal aktif saat ini — semua jadwal sedang dinonaktifkan.';
      }

      if (/jadwal apa saja|daftar jadwal|semua jadwal|list jadwal/.test(lower)) {
        const list = ScheduleManager.getSchedules();
        if (!list.length) return 'Belum ada jadwal pakan yang ditambahkan sama sekali.';
        const lines = list
          .sort((a, b) => a.time.localeCompare(b.time))
          .map((s) => `${s.time} — ${s.label} (${s.enabled ? 'aktif' : 'nonaktif'})`);
        return `Jadwal pakan saat ini:\n${lines.join('\n')}`;
      }

      return null;
    }

    function getReply(userText) {
      const liveReply = getLiveStateReply(userText);
      if (liveReply) return liveReply;

      let best = null;
      let bestScore = 0;
      KNOWLEDGE_BASE.forEach((entry) => {
        const score = scoreMatch(userText, entry.keywords);
        if (score > bestScore) {
          bestScore = score;
          best = entry;
        }
      });
      return best ? best.reply : FALLBACK_REPLY;
    }

    function appendMessage(text, role) {
      const div = document.createElement('div');
      div.className = `message message--${role}`;
      if (role === 'bot') {
        // Aman: teks bot 100% berasal dari kode sendiri (basis pengetahuan
        // statis atau string yang kita rakit sendiri dari Telemetry/dll),
        // tidak pernah dari input pengguna — jadi boleh pakai innerHTML
        // supaya \n bisa jadi baris baru sungguhan.
        div.innerHTML = text.replace(/\n/g, '<br>');
      } else {
        // Pesan pengguna WAJIB textContent — jangan pernah render input
        // pengguna sebagai HTML (celah XSS).
        div.textContent = text;
      }
      messagesEl.appendChild(div);
      messagesEl.scrollTop = messagesEl.scrollHeight;
      return div;
    }

    function showTyping() {
      const div = document.createElement('div');
      div.className = 'message--typing';
      div.innerHTML = '<span></span><span></span><span></span>';
      messagesEl.appendChild(div);
      messagesEl.scrollTop = messagesEl.scrollHeight;
      return div;
    }

    function handleSend(userText) {
      appendMessage(userText, 'user');
      input.value = '';
      suggestions.style.display = 'none';

      const typingEl = showTyping();
      const thinkTime = 500 + Math.random() * 500;

      setTimeout(() => {
        typingEl.remove();
        appendMessage(getReply(userText), 'bot');
      }, thinkTime);
    }

    function handleSubmit(e) {
      e.preventDefault();
      const text = input.value.trim();
      if (!text) return;
      handleSend(text);
    }

    function handleSuggestionClick(e) {
      const chip = e.target.closest('[data-question]');
      if (!chip) return;
      handleSend(chip.dataset.question);
    }

    function openPanel() {
      aquabotRoot.classList.add('is-open');
      fab.setAttribute('aria-expanded', 'true');
      panel.setAttribute('aria-hidden', 'false');

      if (!hasGreeted) {
        hasGreeted = true;
        const typingEl = showTyping();
        setTimeout(() => {
          typingEl.remove();
          appendMessage(
            'Halo! Saya AquaBot 🤖 — bisa jelaskan cara kerja sistem ini, DAN bisa baca kondisi alat sekarang juga. Coba tanya "kondisi alat sekarang gimana?" atau apa saja yang ingin diketahui.',
            'bot'
          );
        }, 500);
      }
      input.focus();
    }
    function closePanel() {
      aquabotRoot.classList.remove('is-open');
      fab.setAttribute('aria-expanded', 'false');
      panel.setAttribute('aria-hidden', 'true');
    }

    function handleDocumentKeydown(e) {
      if (e.key === 'Escape' && aquabotRoot.classList.contains('is-open')) closePanel();
    }

    function init() {
      aquabotRoot = $('#aquabot');
      fab = $('#aquabotFab');
      panel = $('#aquabotPanel');
      closeBtn = $('#aquabotClose');
      messagesEl = $('#aquabotMessages');
      form = $('#aquabotForm');
      input = $('#aquabotInput');
      suggestions = $('#aquabotSuggestions');
      if (!fab || !panel) return;

      fab.addEventListener('click', () => {
        aquabotRoot.classList.contains('is-open') ? closePanel() : openPanel();
      });
      closeBtn.addEventListener('click', closePanel);
      form.addEventListener('submit', handleSubmit);
      suggestions.addEventListener('click', handleSuggestionClick);
      document.addEventListener('keydown', handleDocumentKeydown);
    }

    return { init };
  })();

  /* ==========================================================================
     BOOTSTRAP FINAL
     Semua modul (1–6) diinisialisasi di sini, setelah DOM siap. Urutan ini
     aman terhadap TDZ: setiap panggilan lintas-modul (mis. Telemetry
     memanggil ScheduleManager) hanya terjadi di dalam fungsi yang baru
     dieksekusi setelah seluruh berkas ini selesai diproses — bukan pada
     saat modul didefinisikan.
     ========================================================================== */
  document.addEventListener('DOMContentLoaded', () => {
    Toast.init();
    Navbar.init();
    ScrollReveal.init();
    AmbientCanvas.init();
    ScheduleManager.init();
    ServoControl.init();
    Telemetry.init();
    AquaBot.init();
  });

})();
