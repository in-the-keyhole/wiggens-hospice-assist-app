# Hospice Caregiver Assist – User Stories

This document captures implementation‑ready user stories for a web application that assists loved ones caring for a hospice patient. Each story includes a concise description and testable acceptance criteria. After review, these stories will be created as GitHub issues and cross‑linked here.

Note: Unless specified otherwise, the primary user is a Caregiver (a loved one). Stories include baseline privacy/security expectations appropriate for PHI.

---

## 1) Caregiver Account Sign‑Up and Login

As a Caregiver, I want to create an account and securely sign in so that I can manage my loved one’s hospice care from any device.

Acceptance Criteria
- Given I provide a valid email and strong password, when I sign up, then my account is created and I am signed in.
- Given my account exists, when I enter correct credentials, then I am authenticated and redirected to the dashboard.
- Given I am authenticated, when I am inactive beyond a configurable timeout, then I am securely logged out.
- Given I forgot my password, when I request a reset, then I receive a secure reset flow that lets me set a new password.
- Sessions and all auth traffic are encrypted in transit.

---

## 2) Patient Profile Setup

As a Caregiver, I want to set up a patient profile so that schedules, medications, and contacts are organized around the correct person.

Acceptance Criteria
- Given I am authenticated, when I create a patient profile with name and at least one contact method, then the profile is saved and visible on my dashboard.
- Optional fields include date of birth, hospice organization, primary physician, allergies, and care directives (e.g., DNR).
- Given I edit the profile, when I save changes, then updates are persisted and audit‑logged.

---

## 3) Care Team Contacts Hub

As a Caregiver, I want to store and quickly access important phone numbers (nurse, hospice hotline, pharmacy) so that I can reach help fast.

Acceptance Criteria
- Given I am on the Contacts tab, when I add a new contact with name, role, and phone, then it appears in the list.
- Given I view a contact, when I tap “Call,” then the device initiates a call using the default dialer.
- Contacts can be categorized (Nurse, Hospice Hotline, Social Worker, Pharmacy, Physician).
- Contacts are linked to the patient profile and included in secure exports.

---

## 4) Medication List Management

As a Caregiver, I want to maintain an accurate medication list with dosage, schedule, and instructions so that I can administer meds safely.

Acceptance Criteria
- Fields: medication name, strength, route, dosage instructions, schedule (times or PRN), prescribing info, special instructions, inventory count (optional).
- Given I add a medication, when I save, then it appears in the medication list in schedule order.
- Given a medication is PRN, when logging, then dose time is flexible but requires reason/symptom.
- Editing or archiving a medication keeps historical logs intact.

---

## 5) Medication Administration Logging

As a Caregiver, I want to log each dose given so that I can track adherence and provide accurate information to the nurse.

Acceptance Criteria
- Given a medication exists, when I record a dose with time, amount, and optional notes, then an administration entry is saved.
- Optional fields: pain score before/after (0–10), symptoms, who administered, optional photo (e.g., pill bottle).
- Given I am offline, when I log a dose, then it queues locally and syncs automatically when online.
- Given a log entry is saved, when I view the timeline, then I see entries in chronological order with filters by med/date.

---

## 6) Medication Alerts and Reminders

As a Caregiver, I want timely alerts for scheduled medications so that I don’t miss a dose.

Acceptance Criteria
- Given a med has a schedule, when it’s due, then I receive a notification with med name, dose, and instructions.
- I can acknowledge, snooze (e.g., 10/30/60 minutes), or mark as given.
- Missed dose notifications escalate to a secondary caregiver after a configurable window.
- Quiet hours can be configured to suppress non‑urgent alerts.

---

## 7) Nurse Visit Schedule

As a Caregiver, I want to view and track nurse visits so that I can prepare and log outcomes.

Acceptance Criteria
- I can add upcoming visits with date/time, provider role, and notes.
- Reminders are sent 24 hours and 1 hour before visits (configurable).
- During/after a visit, I can log notes, vitals (optional), and care changes.
- Past visits are viewable and exportable in reports.

---

## 8) Symptom and Pain Tracking

As a Caregiver, I want to track symptoms and pain scores so that I can share trends with the care team.

Acceptance Criteria
- I can record entries with timestamp, symptom tags, free‑text notes, and pain score (0–10).
- Optional vitals: temperature, blood pressure, pulse, respiration (if available).
- I can attach photos (e.g., wound progression) to an entry.
- Entries are filterable by date/symptom and exportable.

---

## 9) Secure Photo Capture and Upload

As a Caregiver, I want to capture and upload photos related to care so that I can document changes securely.

Acceptance Criteria
- I can take a photo using the device camera or select from gallery.
- Before first upload, I must accept a consent notice about storing PHI.
- Photos are encrypted at rest, never embedded in push notifications, and only viewable to authorized users.
- Photos can be linked to a medication log or symptom entry.

---

## 10) Daily Care Checklist

As a Caregiver, I want a configurable checklist (bathing, repositioning, oral care, feeding) so that I can ensure consistent daily care.

Acceptance Criteria
- I can add tasks with frequency (once, times per day, days of week) and notes.
- I receive reminders for scheduled tasks and can mark tasks complete.
- Offline completion is queued and syncs later without data loss.
- I can view daily/weekly completion history.

---

## 11) Emergency Plan Quick Access

As a Caregiver, I want a single “What to do now” screen so that I can act quickly during emergencies.

Acceptance Criteria
- The screen displays hospice hotline and primary nurse contacts with 1‑tap call.
- The screen includes quick guidance for common urgent scenarios (e.g., uncontrolled pain, breathing distress), linking to education content.
- Location and patient context are shown to help communicate with responders (no GPS stored without consent).

---

## 12) Care Education Library

As a Caregiver, I want concise educational content and checklists so that I can perform tasks safely and confidently.

Acceptance Criteria
- Content is organized by topic (meds, comfort, hygiene, equipment) and searchable.
- Articles can include short videos/images and printable guides.
- Content viewed/completed status is tracked per user.

---

## 13) Secure Sharing and Export

As a Caregiver, I want to share a time‑bounded summary with the care team so that they can review meds, symptoms, and visits.

Acceptance Criteria
- I can generate a summary for a date range with sections: Med Logs, Symptoms, Visits, Checklist completion, Contacts.
- I can download as PDF and/or CSV and optionally email a secure link that expires (e.g., 7 days).
- Exports exclude photos by default unless explicitly included.

---

## 14) Multi‑Caregiver Access and Roles

As a Primary Caregiver, I want to invite family members with limited roles so that we can share responsibilities safely.

Acceptance Criteria
- Roles: Owner (full), Caregiver (create/update logs), Viewer (read‑only).
- Invites are email‑based; recipients must accept and create an account.
- Activity is audit‑logged with who/what/when for key actions (med logs, edits, exports).

---

## 15) Notification Preferences and Quiet Hours

As a Caregiver, I want to manage notification channels and quiet hours so that I receive timely but not disruptive alerts.

Acceptance Criteria
- Channels: in‑app, email, SMS (if configured), push (if PWA/native supported).
- I can configure quiet hours per day; urgent alerts (e.g., critical meds) can bypass if enabled.
- Test notification flow validates delivery.

---

## 16) Privacy, Security, and Audit Baseline (HIPAA‑aligned)

As a Product/Compliance Owner, I want baseline safeguards so that PHI is protected and activity is traceable.

Acceptance Criteria
- All traffic uses TLS; PHI is encrypted at rest; access is authenticated and authorized per role.
- Session timeout and re‑authentication for sensitive actions (e.g., exports, role changes).
- Minimal PHI in notifications; no PHI in URLs or logs.
- Audit log captures login/logout, profile edits, med CRUD, log entries, exports, invites, and role changes.
- Data export/download and account deletion are available on request screens (self‑service where possible).

---

Implementation Notes (non‑binding)
- Web first (responsive); consider PWA features for offline queueing and push notifications.
- Store timestamps in UTC; render in user’s local timezone.
- Use accessible UI patterns (WCAG AA), large touch targets, clear language, and multilingual scaffolding (at least en/es where feasible).
- Consider feature flags for SMS/push/exports until configured.

Planned issue links: Will be added below once issues are created.

1. Caregiver Account Sign‑Up and Login – Issue: TBA
2. Patient Profile Setup – Issue: TBA
3. Care Team Contacts Hub – Issue: TBA
4. Medication List Management – Issue: TBA
5. Medication Administration Logging – Issue: TBA
6. Medication Alerts and Reminders – Issue: TBA
7. Nurse Visit Schedule – Issue: TBA
8. Symptom and Pain Tracking – Issue: TBA
9. Secure Photo Capture and Upload – Issue: TBA
10. Daily Care Checklist – Issue: TBA
11. Emergency Plan Quick Access – Issue: TBA
12. Care Education Library – Issue: TBA
13. Secure Sharing and Export – Issue: TBA
14. Multi‑Caregiver Access and Roles – Issue: TBA
15. Notification Preferences and Quiet Hours – Issue: TBA
16. Privacy, Security, and Audit Baseline – Issue: TBA

