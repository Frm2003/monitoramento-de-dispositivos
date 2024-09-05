'use client';

import {
    FormEvent,
    ReactNode,
    RefObject,
    useLayoutEffect,
    useRef,
} from 'react';
import loginStyle from '@/styles/login.module.css';
import axios from 'axios';
import { useRouter } from 'next/navigation';

interface iProps {
    children: ReactNode;
    url: string;
}

export default function FormComponent({ children, url }: iProps): ReactNode {
    const layoutRef: RefObject<HTMLDivElement> = useRef<HTMLDivElement>(null);
    const router = useRouter();

    // GANCHO QUE POSICIONA O ARTICLE NO CENTRO
    useLayoutEffect(() => {
        const handleResize = () => {
            if (layoutRef.current) {
                layoutRef.current.style.height = `${window.innerHeight}px`;
            }
        };

        handleResize();

        window.addEventListener('resize', handleResize);

        return () => window.removeEventListener('resize', handleResize);
    }, []);

    //
    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);
        const formObject = Object.fromEntries(formData.entries());

        const jsonString = JSON.stringify(formObject);

        try {
            const response = await axios.post(url, jsonString, {
                headers: { 'Content-Type': 'application/json' },
            });
            console.log(response.data);

            if ('token' in response.data) {
                localStorage.setItem('token', 'Bearer' + response.data.token);
                router.push('/home');
            } else {
                router.push('/');
            }
        } catch (error) {
            console.log(error);
        }
    };

    return (
        <section className={loginStyle.layout} ref={layoutRef}>
            <article>
                <form onSubmit={(e) => handleSubmit(e)}>
                    {children}
                    <div className={loginStyle.btns}>
                        <button type="submit">Enviar</button>
                    </div>
                </form>
            </article>
        </section>
    );
}
