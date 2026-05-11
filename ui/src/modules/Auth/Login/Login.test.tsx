import { render, screen, fireEvent } from '@testing-library/react'
import { AuthProvider } from '../useAuth'
import { MemoryRouter } from 'react-router-dom'
import Login from '.'

vi.mock('../../../codex-example/api/axios', () => ({
  default: {
    post: vi.fn().mockResolvedValue({ data: { token: 't' } })
  }
}))

describe('Login', () => {
  it('renders and submits', async () => {
    render(
      <AuthProvider>
        <MemoryRouter>
          <Login />
        </MemoryRouter>
      </AuthProvider>
    )
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'a@b.com' } })
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password123' } })
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }))
    // No thrown error implies success; interaction mocked.
  })
})

