import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import Contacts from '.'

vi.mock('../../codex-example/api/contacts', () => ({
  listMyContacts: vi.fn().mockResolvedValue([
    { id: 1, name: 'Nurse Joy', role: 'NURSE', phone: '555-1111', createdAt: '', updatedAt: '' }
  ]),
  addMyContact: vi.fn().mockImplementation(async (c) => ({ id: 2, createdAt: '', updatedAt: '', ...c })),
}))

vi.mock('../Auth/useAuth', () => ({
  useAuth: () => ({ token: 't', login: vi.fn(), register: vi.fn(), logout: vi.fn() })
}))

describe('Contacts', () => {
  it('lists and adds contacts; call link present', async () => {
    render(
      <MemoryRouter>
        <Contacts />
      </MemoryRouter>
    )

    expect(await screen.findByText('Nurse Joy')).toBeInTheDocument()

    fireEvent.change(screen.getByRole('textbox', { name: /Name/i }), { target: { value: 'Pharmacy One' } })
    fireEvent.change(screen.getByRole('textbox', { name: /Phone/i }), { target: { value: '555-2222' } })
    fireEvent.click(screen.getByRole('button', { name: 'Add' }))

    await waitFor(() => expect(screen.getByText('Pharmacy One')).toBeInTheDocument())

    const callLinks = screen.getAllByRole('link', { name: 'Call' })
    expect(callLinks[0].getAttribute('href')).toMatch(/^tel:/)
  })
})
