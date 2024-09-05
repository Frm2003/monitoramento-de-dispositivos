'use client';

import { ReactNode, useEffect, useState } from 'react';
import axios from 'axios';
import { UUID } from 'crypto';
import logStyle from '@/styles/log.module.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faRefresh, faTrash } from '@fortawesome/free-solid-svg-icons';
import { useRouter } from 'next/navigation';

interface iDispositivoReg {
    id: UUID;
    name: string;
    lastPing: string;
    location: string;
    status: string;
}

interface iLog {
    id: UUID;
    text: string;
    dispositivo: iDispositivoReg;
}

export default function Home(): ReactNode {
    const [dispositivo, setDispositivo] = useState<iDispositivoReg>();
    const [listaDeLogs, setListaDeLogs] = useState<iLog[]>([]);

    const router = useRouter();

    const selectById = async () => {
        const response = await axios.get(
            `http://localhost:8080/device/${localStorage.getItem('idDevice')}`,
            {
                headers: { Authorization: localStorage.getItem('token') },
            }
        );
        setDispositivo(response.data);
    };

    const selectLogsByDeviceId = async () => {
        const response = await axios.get(
            `http://localhost:8080/log/${localStorage.getItem('idDevice')}`,
            {
                headers: { Authorization: localStorage.getItem('token') },
            }
        );

        const logs = response.data.map((log: iLog) => ({
            id: log.id,
            text: log.text,
            dispositivo: log.dispositivo,
        }));

        setListaDeLogs(logs);
    };

    const handleDelete = async () => {
        await axios.delete(
            `http://localhost:8080/device/${localStorage.getItem('idDevice')}`,
            {
                headers: {
                    Authorization: localStorage.getItem('token'),
                    'Content-Type': 'application/json',
                },
            }
        );

        router.push('/home');
    };

    useEffect(() => {
        selectById();
        selectLogsByDeviceId();
    }, []);

    const backToHome = () => {
        router.push('/home');
    };

    return (
        <section className={logStyle.layout}>
            <article style={{ width: '80%', margin: 'auto' }}>
                <button
                    style={{
                        border: 'none',
                        borderRadius: '10px',
                        fontSize: '15px',
                        padding: '0.5em',
                    }}
                    onClick={() => backToHome()}
                >
                    Voltar
                </button>
            </article>
            <article className={logStyle.title}>
                {dispositivo != undefined && <h1>{dispositivo.name}</h1>}
                <div>
                    <button onClick={() => selectLogsByDeviceId()}>
                        <FontAwesomeIcon icon={faRefresh} size={'2x'} />
                    </button>
                    <button onClick={() => handleDelete()}>
                        <FontAwesomeIcon icon={faTrash} size={'2x'} />
                    </button>
                </div>
            </article>
            <article>
                <ul>
                    {listaDeLogs.map((log: iLog, index: number) => (
                        <li key={index}>
                            <div>{log.text}</div>
                        </li>
                    ))}
                </ul>
            </article>
        </section>
    );
}
