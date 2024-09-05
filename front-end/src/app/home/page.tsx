'use client';

import { RefObject, useEffect, useLayoutEffect, useRef, useState } from 'react';
import crudStyle from '@/styles/crud.module.css';
import listStyle from '@/styles/list.module.css';
import Modal, { ModalHandles } from '@/components/modal';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import { UUID } from 'crypto';
import { useRouter } from 'next/navigation';

interface iDispositivoNoReg {
    name: string;
    lastPing: string;
    location: string;
    status: string;
}

interface iDispositivoReg extends iDispositivoNoReg {
    id: UUID;
}

export default function Home() {
    const [show, setShow] = useState<boolean>(false);
    const abrirModal = (): void => setShow(true);
    const fecharModal = (): void => setShow(false);

    const modalRef = useRef<ModalHandles>(null);

    // GANCHO QUE POSICIONA O ARTICLE NO CENTRO
    const sectionRef: RefObject<HTMLElement> = useRef<HTMLElement>(null);

    useEffect(() => {
        if (localStorage.getItem('token') == undefined) router.push('/');
    }, []);

    useLayoutEffect(() => {
        const handleResize = () => {
            if (sectionRef.current) {
                sectionRef.current.style.height = `${window.innerHeight}px`;
            }
        };

        handleResize();

        window.addEventListener('resize', handleResize);

        return () => window.removeEventListener('resize', handleResize);
    }, []);

    //REQUISIÇÃO GET DA LISTA DE DISPOSITIVOS NÃO REGISTRADOS NO BANCO
    const [listaDevice, setListaDevice] = useState([]);

    const selectAllFromDataBase = async () => {
        try {
            const response = await axios.get('http://localhost:8080/device', {
                headers: {
                    Authorization: localStorage.getItem('token'),
                },
            });

            const dispositivos = response.data.map((item: iDispositivoReg) => ({
                id: item.id,
                name: item.name,
                lastPing: new Date(item.lastPing).toISOString(),
                location: item.location,
                status: item.status,
            }));

            setListaDevice(dispositivos);
        } catch (error) {
            console.log(error);
        }
    };

    //REQUISIÇÃO GET DA LISTA DE DISPOSITIVOS NÃO REGISTRADOS NO BANCO
    const [listaDeviceUnregistred, setListaDeviceUnregistred] = useState([]);

    const selectAllDevicesUnregistred = async (): Promise<void> => {
        try {
            const response = await axios.get(
                'http://localhost:8080/device/unregistred',
                {
                    headers: {
                        Authorization: localStorage.getItem('token'),
                    },
                }
            );
            const dispositivos = response.data.map(
                (item: iDispositivoNoReg) => ({
                    name: item.name,
                    lastPing: new Date(item.lastPing).toISOString(),
                    location: item.location,
                    status: item.status,
                })
            );

            setListaDeviceUnregistred(dispositivos);
        } catch (error) {
            console.log(error);
        }
    };

    // CHAMADA INICIAL DOS GETS
    useEffect(() => {
        selectAllFromDataBase();
        selectAllDevicesUnregistred();
    }, []);

    //REQUISIÇÃO POST PARA ADICIONAR O DISPOSITVO AO BANCO DE DADOS COM LOG INICIAL
    const insert = async (dispositivo: iDispositivoNoReg): Promise<void> => {
        try {
            const response = await axios.post(
                'http://localhost:8080/device',
                JSON.stringify(dispositivo),
                {
                    headers: {
                        Authorization: localStorage.getItem('token'),
                        'Content-Type': 'application/json',
                    },
                }
            );

            const log = {
                dispositivo: response.data.id,
                text: `${new Date()},\nDispositivo ${response.data.name} registrado`,
            };

            await axios.post('http://localhost:8080/log', JSON.stringify(log), {
                headers: {
                    Authorization: localStorage.getItem('token'),
                    'Content-Type': 'application/json',
                },
            });
            setListaDeviceUnregistred([]);
            selectAllFromDataBase();
            selectAllDevicesUnregistred();
        } catch (error) {
            console.log(error);
        }
    };

    const children = (
        <div className={listStyle.layout}>
            <h3>Dispositivos Não Registrados</h3>
            {listaDeviceUnregistred.length > 0 ? (
                <ul>
                    {listaDeviceUnregistred.map(
                        (dispositivo: iDispositivoNoReg, index: number) => (
                            <li key={index}>
                                <div>
                                    <h3>Nome: {dispositivo.name}</h3>
                                    <p>Status: {dispositivo.status}</p>
                                </div>
                                <div>
                                    <button onClick={() => insert(dispositivo)}>
                                        <FontAwesomeIcon
                                            icon={faPlus}
                                            size={'2x'}
                                        />
                                    </button>
                                </div>
                            </li>
                        )
                    )}
                </ul>
            ) : (
                <p>Loading devices...</p>
            )}
        </div>
    );

    // REQUISIÇAÕ QUE DELETA O DISPOSITIVO E SEUS LOGS
    const handleDelete = async (id: UUID): Promise<void> => {
        await axios.delete(`http://localhost:8080/device/${id}`, {
            headers: {
                Authorization: localStorage.getItem('token'),
                'Content-Type': 'application/json',
            },
        });
        setListaDeviceUnregistred([]);
        selectAllFromDataBase();
        selectAllDevicesUnregistred();
    };

    // VALIDA DISPOSTIVOS ATIVOS NA REDE E ATUALIZA NO BANCO CASO ESTEJAM INATIVOS
    const validatorActive = async (): Promise<void> => {
        const response = await axios.get(
            'http://localhost:8080/device/validateAtivo',
            {
                headers: { Authorization: localStorage.getItem('token') },
            }
        );

        if (response.data.length > 0) {
            const dispositivos = response.data.map((item: iDispositivoReg) => ({
                id: item.id,
                name: item.name,
                lastPing: item.lastPing,
                location: item.location,
                status: '' + item.status,
            }));

            dispositivos.forEach(async (dispostivo: iDispositivoReg) => {
                if (dispostivo.status == 'ATIVO') {
                    await axios.put(
                        `http://localhost:8080/device/${dispostivo.id}`,
                        JSON.stringify({
                            name: dispostivo.name,
                            lastPing: dispostivo.lastPing,
                            location: dispostivo.location,
                            status: 'INATIVO',
                        }),
                        {
                            headers: {
                                Authorization: localStorage.getItem('token'),
                                'Content-Type': 'application/json',
                            },
                        }
                    );

                    const log = {
                        dispositivo: dispostivo.id,
                        text: `${new Date()},\nDispositivo ${response.data.name} está inativo`,
                    };

                    await axios.post(
                        'http://localhost:8080/log',
                        JSON.stringify(log),
                        {
                            headers: {
                                Authorization: localStorage.getItem('token'),
                                'Content-Type': 'application/json',
                            },
                        }
                    );

                    selectAllFromDataBase();
                }
            });
        }
    };

    // VALIDA DISPOSTIVOS INATIVOS NA REDE E ATUALIZA NO BANCO CASO ESTEJAM ATIVOS
    const validatorInative = async (): Promise<void> => {
        const response = await axios.get(
            'http://localhost:8080/device/validateInativo',
            {
                headers: { Authorization: localStorage.getItem('token') },
            }
        );

        if (response.data.length > 0) {
            const dispositivos = response.data.map((item: iDispositivoReg) => ({
                id: item.id,
                name: item.name,
                lastPing: item.lastPing,
                location: item.location,
                status: '' + item.status,
            }));

            dispositivos.forEach(async (dispostivo: iDispositivoReg) => {
                if (dispostivo.status == 'INATIVO') {
                    await axios.put(
                        `http://localhost:8080/device/${dispostivo.id}`,
                        JSON.stringify({
                            name: dispostivo.name,
                            lastPing: dispostivo.lastPing,
                            location: dispostivo.location,
                            status: 'ATIVO',
                        }),
                        {
                            headers: {
                                Authorization: localStorage.getItem('token'),
                                'Content-Type': 'application/json',
                            },
                        }
                    );

                    const log = {
                        dispositivo: dispostivo.id,
                        text: `${new Date()},\nDispositivo ${dispostivo.name} está ativo`,
                    };

                    await axios.post(
                        'http://localhost:8080/log',
                        JSON.stringify(log),
                        {
                            headers: {
                                Authorization: localStorage.getItem('token'),
                                'Content-Type': 'application/json',
                            },
                        }
                    );

                    selectAllFromDataBase();
                }
            });
        }
    };

    // HOOK QUE DEFINE O INTERVALO DE TEMPO PARA AS AVALIAÇÕES
    useEffect(() => {
        const logMessage = () => {
            validatorActive();
            validatorInative();
        };

        const intervalId = setInterval(logMessage, 2500);

        return () => clearInterval(intervalId);
    }, []);

    const router = useRouter();

    const trocaDeTela = (id: UUID) => {
        localStorage.setItem('idDevice', id);
        router.push(`/home/${id}`);
    };

    return (
        <>
            <section className={crudStyle.layout} ref={sectionRef}>
                <article className={crudStyle.title}>
                    <h2>Dispostivos</h2>
                    <button onClick={() => abrirModal()}>
                        <FontAwesomeIcon icon={faPlus} size={'2x'} />
                    </button>
                </article>
                <article>
                    <ul>
                        {listaDevice.map(
                            (item: iDispositivoReg, index: number) => (
                                <li key={index}>
                                    <div>
                                        <h3
                                            onClick={() => trocaDeTela(item.id)}
                                        >
                                            {item.name}
                                        </h3>
                                        <p>{item.status}</p>
                                        <p>
                                            Ultimo ping:{' '}
                                            {new Date(
                                                item.lastPing
                                            ).toLocaleDateString()}
                                        </p>
                                    </div>
                                    <div>
                                        <button
                                            onClick={() =>
                                                handleDelete(item.id)
                                            }
                                        >
                                            <FontAwesomeIcon
                                                icon={faTrash}
                                                size={'2x'}
                                            />
                                        </button>
                                    </div>
                                </li>
                            )
                        )}
                    </ul>
                </article>
            </section>
            <Modal fecharModal={fecharModal} show={show} ref={modalRef}>
                {children}
            </Modal>
        </>
    );
}
