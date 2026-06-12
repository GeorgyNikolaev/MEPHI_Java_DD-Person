import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { formatErrorMessage } from '@/api/errors';
import { Button } from '@/components/common/Button';
import { ErrorAlert } from '@/components/common/ErrorAlert';
import { Input } from '@/components/common/Input';
import { useAuth } from '@/hooks/useAuth';

export function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await register({ email, password, displayName });
      navigate('/dashboard');
    } catch (err) {
      setError(formatErrorMessage(err, 'Не удалось зарегистрироваться'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="panel auth-card">
        <h1>Регистрация</h1>
        <p className="auth-subtitle">Создайте аккаунт для генерации портретов</p>
        {error && <ErrorAlert message={error} />}
        <form onSubmit={handleSubmit} className="form-grid">
          <Input
            label="Отображаемое имя"
            name="displayName"
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            required
            maxLength={100}
          />
          <Input
            label="Email"
            type="email"
            name="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            autoComplete="email"
          />
          <Input
            label="Пароль"
            type="password"
            name="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={8}
            hint="Не менее 8 символов"
            autoComplete="new-password"
          />
          <Button type="submit" disabled={submitting}>
            {submitting ? 'Регистрация…' : 'Зарегистрироваться'}
          </Button>
        </form>
        <p style={{ marginTop: '1rem', textAlign: 'center' }}>
          Уже есть аккаунт? <Link to="/login">Войти</Link>
        </p>
      </div>
    </div>
  );
}
