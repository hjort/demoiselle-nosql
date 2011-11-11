/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.cassandra.example.test;

import java.io.IOException;

//import me.prettyprint.cassandra.testutils.EmbeddedServerHelper;

import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class for test cases that need access to EmbeddedServerHelper
 * 
 * @author Nate McCall (nate@vervewireless.com)
 */
public abstract class EmbeddedServerExecution {

//	private static EmbeddedServerHelper embedded;

//	protected CassandraClientPool pools;
//	protected CassandraHostConfigurator cassandraHostConfigurator;

	/**
	 * Set embedded cassandra up and spawn it in a new thread.
	 * 
	 * @throws TTransportException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	/*
	@BeforeClass
	public static void setup() throws TTransportException, IOException, InterruptedException {
		embedded = new EmbeddedServerHelper();
		embedded.setup();
	}

	@AfterClass
	public static void teardown() throws IOException {
		embedded.teardown();
		embedded = null;
	}
	*/

	/*
	protected void setupClient() {
		cassandraHostConfigurator = new CassandraHostConfigurator("127.0.0.1:9170");
		pools = CassandraClientPoolFactory.INSTANCE.createNew(cassandraHostConfigurator);
	}*/

}
