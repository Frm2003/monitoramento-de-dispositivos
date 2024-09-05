import FormComponent from '@/components/formComponent';
import Link from 'next/link';

export default function Home() {
    const children = (
        <>
            <h2>Logar</h2>
            <div>
                <div>
                    <label>Nome</label>
                    <input
                        type="text"
                        name="nome"
                        placeholder="digite seu nome"
                    />
                </div>
                <div>
                    <label>Email</label>
                    <input
                        type="email"
                        name="email"
                        placeholder="digite o email"
                    />
                </div>
                <div>
                    <label>Senha</label>
                    <input
                        type="password"
                        name="senha"
                        placeholder="digite a senha"
                    />
                </div>
                <Link href={'/'}>Login</Link>
            </div>
        </>
    );

    return (
        <FormComponent url={'http://localhost:8080/user/login'}>
            {children}
        </FormComponent>
    );
}
