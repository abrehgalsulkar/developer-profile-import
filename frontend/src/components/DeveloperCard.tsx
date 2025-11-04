import './DeveloperCard.css';
import type { DeveloperProfile } from '../types';

interface Props {
  developer: DeveloperProfile;
}

export function DeveloperCard({ developer }: Props) {
  const {
    firstName,
    lastName,
    designation,
    verified,
    hourlyRate,
    experienceYears,
    totalProjectCompletion,
    availabilities,
    workLocations,
    technologies,
    languages,
    about,
  } = developer;

  const availabilityDisplay =
    availabilities.length > 0 ? availabilities.join(', ') : 'Not specified';
  const experienceDisplay =
    experienceYears != null ? `${experienceYears} yrs experience` : 'Not specified';
  const projectDisplay =
    totalProjectCompletion != null ? `${totalProjectCompletion} projects` : 'Not specified';
  const workLocationsDisplay =
    workLocations.length > 0 ? workLocations.join(', ') : undefined;

  return (
    <article className="developer-card">
      <header className="developer-card__header">
        <div className="developer-card__header-left">
          <div className="developer-card__avatar" aria-hidden="true" />
          <div className="developer-card__identity">
            <div className="developer-card__name-row">
              <span className="developer-card__name">
                {firstName} {lastName}
              </span>
              {verified && <span className="developer-card__badge">Verified</span>}
            </div>
            {designation && <div className="developer-card__designation">{designation}</div>}
            {workLocationsDisplay && (
              <div className="developer-card__locations">{workLocationsDisplay}</div>
            )}
          </div>
        </div>
        {hourlyRate != null && (
          <div className="developer-card__rate">
            <span className="developer-card__rate-amount">${hourlyRate}</span>
            <span className="developer-card__rate-label">/ hour</span>
          </div>
        )}
      </header>

      <section className="developer-card__highlights">
        <div className="developer-card__highlight">
          <span className="developer-card__highlight-label">Availability</span>
          <span className="developer-card__highlight-value">{availabilityDisplay}</span>
        </div>
        <div className="developer-card__highlight">
          <span className="developer-card__highlight-label">Experience</span>
          <span className="developer-card__highlight-value">{experienceDisplay}</span>
        </div>
        <div className="developer-card__highlight">
          <span className="developer-card__highlight-label">Projects</span>
          <span className="developer-card__highlight-value">{projectDisplay}</span>
        </div>
      </section>

      {availabilities.length > 0 && (
        <section>
          <h3 className="developer-card__section-title">Availability</h3>
          <div className="developer-card__chip-row">
            {availabilities.map((item) => (
              <span key={item} className="developer-card__chip">
                {item}
              </span>
            ))}
          </div>
        </section>
      )}

      {workLocations.length > 0 && (
        <section>
          <h3 className="developer-card__section-title">Work Locations</h3>
          <div className="developer-card__chip-row">
            {workLocations.map((item) => (
              <span key={item} className="developer-card__chip">
                {item}
              </span>
            ))}
          </div>
        </section>
      )}

      {languages.length > 0 && (
        <section>
          <h3 className="developer-card__section-title">Languages</h3>
          <div className="developer-card__chip-row">
            {languages.map((lang, index) => (
              <span key={`${lang.language}-${index}`} className="developer-card__chip">
                {lang.language || 'Unknown'} ({lang.proficiency || 'N/A'})
              </span>
            ))}
          </div>
        </section>
      )}

      {technologies.length > 0 && (
        <section>
          <h3 className="developer-card__section-title">Skills</h3>
          <div className="developer-card__chip-row">
            {technologies.map((tech) => (
              <span key={tech} className="developer-card__chip">
                {tech}
              </span>
            ))}
          </div>
        </section>
      )}

      {about && <p className="developer-card__about">{about}</p>}

      <button type="button" className="developer-card__hire-button">
        Hire Developer
      </button>
    </article>
  );
}
